package org.jboss.tools.forge.ui.ext.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.jboss.tools.forge.core.runtime.ForgeRuntime;
import org.jboss.tools.forge.ext.core.runtime.FurnaceRuntime;
import org.jboss.tools.forge.ui.ext.ForgeUIPlugin;
import org.jboss.tools.forge.ui.ext.dialog.UICommandListDialog;
import org.jboss.tools.forge.ui.ext.util.FurnaceHelper;

public class ForgeCommandHandler extends AbstractHandler {
	
	@Override
	public Object execute(ExecutionEvent event) {
		try {
			final IWorkbenchWindow window = HandlerUtil
					.getActiveWorkbenchWindowChecked(event);
			if (!ForgeRuntime.STATE_RUNNING.equals(FurnaceRuntime.INSTANCE.getState())) {
				Job job = FurnaceHelper.createStartFurnaceJob();
				job.addJobChangeListener(new JobChangeAdapter() {
					@Override
					public void done(IJobChangeEvent event) {
						Display.getDefault().asyncExec(new Runnable() {
							@Override
							public void run() {
								new UICommandListDialog(window).open();
							}				
						});
					}
				});
				job.schedule();
			} else {
				new UICommandListDialog(window).open();
			}
		} catch (Exception e) {
			ForgeUIPlugin.log(e);
		}
		return null;
	}
		
}
