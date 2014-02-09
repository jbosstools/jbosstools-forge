package org.jboss.tools.forge.ui.ext.util;

import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.jboss.tools.forge.core.process.ForgeRuntime;
import org.jboss.tools.forge.ext.core.runtime.FurnaceRuntime;

public class FurnaceHelper {

	public static void startFurnace() {
		final ForgeRuntime runtime = FurnaceRuntime.INSTANCE;
		if (runtime == null || ForgeRuntime.STATE_RUNNING.equals(runtime.getState())) return;
		createStartFurnaceJob().schedule();
	}
	
	public static void stopFurnace() {
		final ForgeRuntime runtime = FurnaceRuntime.INSTANCE;
		if (runtime == null || ForgeRuntime.STATE_NOT_RUNNING.equals(runtime.getState())) return;
		Job job = new Job("Stopping Forge " + runtime.getVersion()) {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				runtime.stop(monitor);
				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}
	
	public static Job createStartFurnaceJob() {
		final FurnaceRuntime runtime = FurnaceRuntime.INSTANCE;
		final String version = runtime.getVersion();
		WorkspaceJob job = new WorkspaceJob("Starting Forge" + version) {					
			@Override
			public IStatus runInWorkspace(IProgressMonitor monitor)
					throws CoreException {
				String taskName = "Please wait while Forge " + version + " is started.";
				monitor.beginTask(taskName, IProgressMonitor.UNKNOWN);
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
		job.setUser(true);	
		return job;
	}
	
}
