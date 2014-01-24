package org.jboss.tools.forge.ui.part;

import org.eclipse.jface.action.Action;
import org.jboss.tools.forge.ui.console.ForgeConsole;

public class ForgeConsoleShowAction extends Action {
	
	private ForgeConsoleView forgeConsoleView = null;
	private ForgeConsole forgeConsole = null;
	
	public ForgeConsoleShowAction(ForgeConsoleView forgeConsoleView, ForgeConsole forgeConsole) {
		super(forgeConsole.toString(), AS_RADIO_BUTTON);
		this.forgeConsoleView = forgeConsoleView;
		this.forgeConsole = forgeConsole;
	}
	
	@Override
	public void run() {
		forgeConsoleView.setMessage(forgeConsole.toString());
	}

}
