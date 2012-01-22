package org.jboss.tools.forge.ui.util;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.forge.core.preferences.ForgeRuntimesPreferences;
import org.jboss.tools.forge.core.process.ForgeRuntime;
import org.jboss.tools.forge.ui.document.ForgeDocument;
import org.jboss.tools.forge.ui.part.ForgeView;

public class ForgeHelper {
	
	public static ForgeView getForgeView() {
		IWorkbench workbench = PlatformUI.getWorkbench();
		if (workbench == null) return null;
		IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
		if (workbenchWindow == null) return null;
		IWorkbenchPage workbenchPage = workbenchWindow.getActivePage();
		if (workbenchPage == null) return null;
		IViewPart viewPart = workbenchPage.findView(ForgeView.ID);
		if (viewPart != null && viewPart instanceof ForgeView) {
			return (ForgeView)viewPart;
		} else {
			return null;
		}
	}

	public static void startForge() {
		final ForgeRuntime runtime = ForgeRuntimesPreferences.INSTANCE.getDefaultRuntime();
		if (runtime == null || ForgeRuntime.STATE_RUNNING.equals(runtime.getState())) return;
 		ForgeDocument.INSTANCE.connect(runtime);
		Job job = new Job("Starting Forge") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				runtime.start(monitor);
				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}
	
	public static void stopForge() {
		final ForgeRuntime runtime = ForgeRuntimesPreferences.INSTANCE.getDefaultRuntime();
		if (runtime == null || ForgeRuntime.STATE_NOT_RUNNING.equals(runtime.getState())) return;
		Job job = new Job("Stopping Forge") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				runtime.stop(monitor);
				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}
	
	public static boolean isForgeRunning() {
		ForgeRuntime runtime = ForgeRuntimesPreferences.INSTANCE.getDefaultRuntime();
		return runtime != null && ForgeRuntime.STATE_RUNNING.equals(runtime.getState());
	}

	public static boolean isForgeStarting() {
		ForgeRuntime runtime = ForgeRuntimesPreferences.INSTANCE.getDefaultRuntime();
		return runtime != null && ForgeRuntime.STATE_STARTING.equals(runtime.getState());
	}

}
