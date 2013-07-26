package org.jboss.tools.forge.ui.ext.handlers;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
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
				ProgressMonitorDialog pmd = new ProgressMonitorDialog(shell);
				pmd.run(true, false, new IRunnableWithProgress() {				
					@Override
					public void run(IProgressMonitor monitor) throws InvocationTargetException,
							InterruptedException {
						String taskName = "Please wait while Forge 2 is started.";
						monitor.beginTask(taskName, IProgressMonitor.UNKNOWN);
						FurnaceService.INSTANCE.waitUntilContainerIsStarted();
						// hack to make progress monitor stick until all commands are loaded
						if (selection instanceof IStructuredSelection) {
							new WizardDialogHelper(shell, (IStructuredSelection)selection).getAllCandidatesAsMap();
						}
					}
				});
			}
			new UICommandListDialog(window).open();
		} catch (Exception e) {
			ForgeUIPlugin.log(e);
		}
		return null;
	}
		
}
