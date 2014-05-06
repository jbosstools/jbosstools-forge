package org.jboss.tools.forge.ui.util;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.jboss.tools.forge.core.preferences.ForgeCorePreferences;
import org.jboss.tools.forge.core.runtime.ForgeRuntime;
import org.jboss.tools.forge.core.runtime.ForgeRuntimeState;
import org.jboss.tools.forge.ui.internal.document.ForgeDocument;
import org.jboss.tools.forge.ui.internal.ext.util.FurnaceHelper;

public class ForgeHelper {
	
	public static void start(ForgeRuntime runtime) {
		FurnaceHelper.createStartRuntimeJob(runtime).schedule();
	}
	
	public static void startForge() {
		final ForgeRuntime runtime = ForgeCorePreferences.INSTANCE.getDefaultRuntime();
		if (runtime == null || ForgeRuntimeState.RUNNING.equals(runtime.getState())) return;
 		ForgeDocument.INSTANCE.connect(runtime);
		Job job = new Job("Starting Forge " + runtime.getVersion()) {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				runtime.start(monitor);
				if (runtime.getErrorMessage() != null) {
					Display.getDefault().asyncExec(new Runnable() {
						@Override
						public void run() {
							MessageDialog.openError(
									null, 
									"Forge Startup Error", 
									runtime.getErrorMessage());
						}			
					});
				}
				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}
	
	public static void stopForge() {
		final ForgeRuntime runtime = ForgeCorePreferences.INSTANCE.getDefaultRuntime();
		if (runtime == null || ForgeRuntimeState.STOPPED.equals(runtime.getState())) return;
		Job job = new Job("Stopping Forge " + runtime.getVersion()) {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				runtime.stop(monitor);
				return Status.OK_STATUS;
			}
		};
		job.schedule();
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
	
}
