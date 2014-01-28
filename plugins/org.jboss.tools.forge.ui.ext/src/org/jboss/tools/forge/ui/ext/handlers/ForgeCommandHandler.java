package org.jboss.tools.forge.ui.ext.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.jboss.tools.forge.ext.core.ForgeCorePlugin;
import org.jboss.tools.forge.ext.core.FurnaceService;
import org.jboss.tools.forge.ui.ext.ForgeUIPlugin;
import org.jboss.tools.forge.ui.ext.dialog.UICommandListDialog;
import org.jboss.tools.forge.ui.ext.dialog.WizardDialogHelper;

public class ForgeCommandHandler extends AbstractHandler {
	
	private Shell shell;
	
	@Override
	public Object execute(ExecutionEvent event) {
		try {
			shell = HandlerUtil.getActiveShell(event);
			final IWorkbenchWindow window = HandlerUtil
					.getActiveWorkbenchWindowChecked(event);
			final ISelection selection = window.getSelectionService().getSelection();
			if (!FurnaceService.INSTANCE.getContainerStatus().isStarted()) {
				WorkspaceJob job = new WorkspaceJob("Starting Forge 2") {					
					@Override
					public IStatus runInWorkspace(IProgressMonitor monitor)
							throws CoreException {
						String taskName = "Please wait while Forge 2 is started.";
						try {
							monitor.beginTask(taskName, IProgressMonitor.UNKNOWN);
							ForgeCorePlugin.getDefault().startFurnace();
							FurnaceService.INSTANCE.waitUntilContainerIsStarted();
							// hack to make progress monitor stick until all commands are loaded
							if (selection instanceof IStructuredSelection) {
								new WizardDialogHelper(shell, (IStructuredSelection)selection).getAllCandidatesAsMap();
							}
						} catch (InterruptedException e) {
							ForgeUIPlugin.log(e);
						}
						return Status.OK_STATUS;
					}
				};
				job.setUser(true);
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
