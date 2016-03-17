/**
 * Copyright (c) Red Hat, Inc., contributors and others 2013 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.forge.ui.util;

import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.jboss.forge.addon.database.tools.connections.ConnectionProfileManagerProvider;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.furnace.services.Imported;
import org.jboss.tools.forge.core.furnace.FurnaceRuntime;
import org.jboss.tools.forge.core.furnace.FurnaceService;
import org.jboss.tools.forge.core.runtime.ForgeRuntime;
import org.jboss.tools.forge.ui.internal.ForgeUIPlugin;
import org.jboss.tools.forge.ui.internal.console.ForgeConsoleManager;
import org.jboss.tools.forge.ui.internal.ext.database.ConnectionProfileManagerImpl;
import org.jboss.tools.forge.ui.internal.ext.importer.ImportEclipseProjectListener;
import org.jboss.tools.forge.ui.internal.part.ForgeConsoleView;

public class ForgeHelper {

	private ForgeHelper() {
	}

	public static void start(ForgeRuntime runtime) {
		createStartRuntimeJob(runtime).schedule();
	}
	
	public static void stop(ForgeRuntime runtime) {
		createStopRuntimeJob(runtime).schedule();
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
	
	private static IWorkbenchPage getActiveWorkbenchPage() {
		IWorkbench workbench = PlatformUI.getWorkbench();
		if (workbench == null) return null;
		IWorkbenchWindow activeWorkbenchWindow = workbench.getActiveWorkbenchWindow();
		if (activeWorkbenchWindow == null) return null;
		return activeWorkbenchWindow.getActivePage();
	}
	
	public static void showForgeConsole(ForgeRuntime forgeRuntime) {
		try {
			IWorkbenchPage activeWorkbenchPage = getActiveWorkbenchPage();
			IViewPart forgeConsoleView = activeWorkbenchPage.showView(ForgeConsoleView.ID);
			if (forgeConsoleView == null) return;
			((ForgeConsoleView)forgeConsoleView).showForgeConsole(ForgeConsoleManager.INSTANCE.getConsole(forgeRuntime));
		} catch (Exception e) {
			ForgeUIPlugin.log(e);
		}
	}
	
	public static void showRuntime(ForgeRuntime forgeRuntime) {
		try {
			ForgeConsoleView forgeConsoleView = findForgeConsoleView();
			if (forgeConsoleView == null) return;
			forgeConsoleView.showForgeConsole(ForgeConsoleManager.INSTANCE.getConsole(forgeRuntime));
		} catch (Exception e) {
			ForgeUIPlugin.log(e);
		}
	}
	
	public static ForgeConsoleView findForgeConsoleView() {
		IWorkbenchPage activeWorkbenchPage = getActiveWorkbenchPage();
		IViewPart forgeConsoleView = activeWorkbenchPage.findView(ForgeConsoleView.ID);
		return forgeConsoleView == null ? null : (ForgeConsoleView)forgeConsoleView;
	}

}
