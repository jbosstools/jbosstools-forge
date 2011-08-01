package org.jboss.tools.forge.ui.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.jboss.tools.forge.core.process.ForgeRuntime;
import org.jboss.tools.forge.ui.dialog.ForgeCommandListDialog;
import org.jboss.tools.forge.ui.part.ConsoleView;

public class ForgeCommandListHandler extends AbstractHandler {

	public Object execute(ExecutionEvent executionEvent) {

		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(executionEvent);
		if (window == null) {
			return null;
		}
		IViewPart part = window.getActivePage().findView(ConsoleView.ID);
		if (part == null || !(part instanceof ConsoleView)) {
			return null;
		}
		
		ForgeRuntime runtime = ((ConsoleView)part).getRuntime();
		if (runtime == null || !(ForgeRuntime.STATE_RUNNING.equals(runtime.getState()))) {
			return null;
		}
		
		String result = runtime.sendCommand("command-list-query");
		
		new ForgeCommandListDialog(window, result).open();
		
		return null;
	}

}