package org.jboss.tools.forge.ui.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.jboss.tools.forge.core.preferences.ForgeRuntimesPreferences;
import org.jboss.tools.forge.core.process.ForgeRuntime;
import org.jboss.tools.forge.ui.ForgeUIPlugin;
import org.jboss.tools.forge.ui.dialog.ForgeCommandListDialog;
import org.jboss.tools.forge.ui.part.ForgeView;
import org.jboss.tools.forge.ui.util.ForgeHelper;

public class ForgeCommandListHandler extends AbstractHandler {

	public Object execute(ExecutionEvent executionEvent) {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(executionEvent);
		if (window == null) {
			return null;
		}				
		showForgeView(window);
		ForgeRuntime runtime = ForgeRuntimesPreferences.INSTANCE.getDefaultRuntime();
		if (ForgeHelper.isForgeStarting()) {
			showWaitUntilStartedMessage();
		} else if (!ForgeHelper.isForgeRunning()) {
			askUserToStartRuntime(); 
		}
		if (runtime != null && ForgeRuntime.STATE_RUNNING.equals(runtime.getState())) {
			new ForgeCommandListDialog(window, runtime).open();
		}
		return null;
	}
	
	private void showWaitUntilStartedMessage() {
		MessageDialog.openInformation(null, "Forge Starting", "Forge is starting. Please wait until the Forge runtime is started");
	}
	
	private void askUserToStartRuntime() {
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
					ForgeHelper.startForge();
				}				
			});
		}
	}
	
	private void showForgeView(IWorkbenchWindow window) {
		try {
			window.getActivePage().showView(ForgeView.ID);
		} catch (PartInitException e) {
			ForgeUIPlugin.log(e);
		}								
	}

}