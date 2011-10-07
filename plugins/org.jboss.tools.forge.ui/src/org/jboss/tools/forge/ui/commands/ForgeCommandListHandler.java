package org.jboss.tools.forge.ui.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.jboss.tools.forge.core.process.ForgeRuntime;
import org.jboss.tools.forge.ui.ForgeUIPlugin;
import org.jboss.tools.forge.ui.dialog.ForgeCommandListDialog;
import org.jboss.tools.forge.ui.part.ForgeView;

public class ForgeCommandListHandler extends AbstractHandler {

	public Object execute(ExecutionEvent executionEvent) {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(executionEvent);
		if (window == null) {
			return null;
		}				
		ForgeRuntime runtime = getForgeRuntime(window);
		if (isStarting(runtime)) {
			showWaitUntilStartedMessage();
		} else if (!(isRunning(runtime))) {
			askUserToStartRuntime(window); 
		}
		if (runtime != null && ForgeRuntime.STATE_RUNNING.equals(runtime.getState())) {
			new ForgeCommandListDialog(window, runtime).open();
		}
		return null;		
	}
	
	private boolean isStarting(ForgeRuntime runtime) {
		return runtime != null && ForgeRuntime.STATE_STARTING.equals(runtime.getState());
	}
	
	private boolean isRunning(ForgeRuntime runtime) {
		return runtime != null && ForgeRuntime.STATE_RUNNING.equals(runtime.getState());
	}
	
	private ForgeRuntime getForgeRuntime(IWorkbenchWindow window) {
		IViewPart part = window.getActivePage().findView(ForgeView.ID);
		if (part != null && part instanceof ForgeView) {
			return ((ForgeView)part).getRuntime();
		} else {
			return null;
		}
	}
	
	private void showWaitUntilStartedMessage() {
		MessageDialog.openInformation(null, "Forge Starting", "Forge is starting. Please wait until the Forge runtime is started");
	}
	
	private void askUserToStartRuntime(final IWorkbenchWindow window) {
		boolean start = MessageDialog.open(
				MessageDialog.QUESTION, 
				null, 
				"Forge Not Running", 
				"Forge is not running. Do you want to start the Forge runtime?", 
				SWT.NONE);
		if (start) {
			Display.getCurrent().asyncExec(new Runnable() {
				@Override
				public void run() {
					try {
						IViewPart part = window.getActivePage().showView(ForgeView.ID);
						if (part != null && part instanceof ForgeView) {
							((ForgeView)part).startForge();
						}	 
					} catch (PartInitException e) {
						ForgeUIPlugin.log(e);
					}						
				}				
			});
		}
	}

}