package org.jboss.tools.forge.ui.util;

import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.jboss.forge.addon.database.tools.connections.ConnectionProfileManagerProvider;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.furnace.services.Imported;
import org.jboss.tools.forge.core.furnace.FurnaceRuntime;
import org.jboss.tools.forge.core.furnace.FurnaceService;
import org.jboss.tools.forge.core.preferences.ForgeCorePreferences;
import org.jboss.tools.forge.core.runtime.ForgeRuntime;
import org.jboss.tools.forge.core.runtime.ForgeRuntimeState;
import org.jboss.tools.forge.ui.internal.ForgeUIPlugin;
import org.jboss.tools.forge.ui.internal.ext.database.ConnectionProfileManagerImpl;
import org.jboss.tools.forge.ui.internal.ext.importer.ImportEclipseProjectListener;

public class ForgeHelper {
	
	public static void start(ForgeRuntime runtime) {
		createStartRuntimeJob(runtime).schedule();
	}
	
	public static void stop(ForgeRuntime runtime) {
		createStopRuntimeJob(runtime).schedule();
	}
	
	public static boolean isForgeRunning() {
		ForgeRuntime runtime = ForgeCorePreferences.INSTANCE.getDefaultRuntime();
		return runtime != null && ForgeRuntimeState.RUNNING.equals(runtime.getState());
	}

	public static boolean isForgeStarting() {
		ForgeRuntime runtime = ForgeCorePreferences.INSTANCE.getDefaultRuntime();
		return runtime != null && ForgeRuntimeState.STARTING.equals(runtime.getState());
	}
	
	public static ForgeRuntime getDefaultRuntime() {
		return ForgeCorePreferences.INSTANCE.getDefaultRuntime();
	}
	
	public static Job createStartRuntimeJob(final ForgeRuntime runtime) {
		final String version = runtime.getVersion();
		WorkspaceJob job = new WorkspaceJob("Starting JBoss Forge " + version) {
			@Override
			public IStatus runInWorkspace(IProgressMonitor monitor)
					throws CoreException {
				String taskName = "Please wait while JBoss Forge " + version + " is started.";
				monitor.beginTask(taskName, IProgressMonitor.UNKNOWN);
				runtime.start(monitor);
				if (runtime instanceof FurnaceRuntime) {
					initializeFurnaceRuntime();
				}
				if (runtime.getErrorMessage() != null) {
					Display.getDefault().asyncExec(new Runnable() {
						@Override
						public void run() {
							MessageDialog.openError(
									null,
									"JBoss Forge Startup Error",
									runtime.getErrorMessage());
						}
					});
				}
				return Status.OK_STATUS;
			}
		};
		job.setUser(true);
		return job;
	}
	
	public static Job createStopRuntimeJob(final ForgeRuntime runtime) {
		Job job = new Job("Stopping JBoss Forge " + runtime.getVersion()) {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				runtime.stop(monitor);
				return Status.OK_STATUS;
			}
		};
		job.setUser(true);
		return job;
	}
	
	private static void initializeFurnaceRuntime() {
		FurnaceService forgeService = FurnaceService.INSTANCE;
		try {
			forgeService.waitUntilContainerIsStarted();
		} catch (InterruptedException e) {
			ForgeUIPlugin.log(e);
			return;
		}
		ProjectFactory projectFactory;
		while ((projectFactory = forgeService
				.lookup(ProjectFactory.class)) == null) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				break;
			}
		}
		if (projectFactory != null) {
			projectFactory
					.addProjectListener(ImportEclipseProjectListener.INSTANCE);
		}
		try {
			Imported<ConnectionProfileManagerProvider> imported = forgeService
					.lookupImported(ConnectionProfileManagerProvider.class);
			if (imported != null) {
				ConnectionProfileManagerProvider provider = imported
						.get();
				provider.setConnectionProfileManager(new ConnectionProfileManagerImpl());
			}
		} catch (Throwable t) {
			ForgeUIPlugin.log(t);
		}
	}

}
