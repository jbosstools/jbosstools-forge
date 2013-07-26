package org.jboss.tools.forge.ui.ext.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.jboss.tools.forge.ext.core.FurnaceService;
import org.jboss.tools.forge.ui.ext.ForgeUIPlugin;
import org.jboss.tools.forge.ui.ext.dialog.UICommandListDialog;

public class ForgeCommandHandler extends AbstractHandler {
	
	private ExecutionEvent event;
	
	private Job checkFurnaceStatusJob = new Job("Check Furnace Status") {
		@Override
		protected IStatus run(IProgressMonitor arg0) {
			try {
				FurnaceService.INSTANCE.waitUntilContainerIsStarted();
			} catch (InterruptedException e) {
				ForgeUIPlugin.log(e);
			}
			return Status.OK_STATUS;
		}
	};
	
	private IJobChangeListener jobChangeListener = new JobChangeAdapter() {
		@Override
		public void done(IJobChangeEvent arg0) {
			checkFurnaceStatusJob.removeJobChangeListener(jobChangeListener);
			openUICommandListDialog();
		}
	};

	@Override
	public Object execute(ExecutionEvent event) {
		this.event = event;
		checkFurnaceStatusJob.addJobChangeListener(jobChangeListener);
		checkFurnaceStatusJob.schedule();
		return null;
//		try {
//			
//			FurnaceService.INSTANCE.waitUntilContainerIsStarted();
//		} catch (InterruptedException e) {
//			throw new ExecutionException("Container not started", e);
//		}
//		IWorkbenchWindow window = HandlerUtil
//				.getActiveWorkbenchWindowChecked(event);
//		return new UICommandListDialog(window).open();
	}
		
	private void openUICommandListDialog() {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				try {
					IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
					new UICommandListDialog(window).open();
				} catch (ExecutionException e) {
					ForgeUIPlugin.log(e);
				}
			}			
		});
	}

}
