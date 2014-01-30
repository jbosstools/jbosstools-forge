package org.jboss.tools.forge.ui.part;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.jboss.tools.forge.ui.actions.ForgeConsoleDropdownAction;
import org.jboss.tools.forge.ui.console.ForgeConsole;
import org.jboss.tools.forge.ui.console.ForgeConsoleManager;

public class ForgeConsoleView extends ViewPart {
	
	public static final String FORGE_CONSOLE_ACTION_GROUP = "org.jboss.tools.forge.ui.console.actions";

	private Composite parent = null;
	private ForgeConsolePageBook forgeConsolePageBook = null;
	private ForgeConsole current = null;
	
	@Override
	public void createPartControl(Composite parent) {
		this.parent = parent;
		createActions();
		createPageBook();
		showForgeConsole(ForgeConsoleManager.INSTANCE.getConsoles()[0]);
	}

	@Override
	public void setFocus() {
		// nothing to do (yet)
	}
	
	public void showForgeConsole(ForgeConsole forgeConsole) {
		forgeConsolePageBook.showForgeConsole(forgeConsole);
	}
	
	public ForgeConsole getConsole() {
		return current;
	}
	
	private void createPageBook() {
		forgeConsolePageBook = new ForgeConsolePageBook(this, parent);
	}
	
	private void createActions() {
		ForgeConsoleDropdownAction action = new ForgeConsoleDropdownAction(this);
		IToolBarManager toolBarManager = getViewSite().getActionBars().getToolBarManager();
		toolBarManager.add(new Separator(FORGE_CONSOLE_ACTION_GROUP));
		// additional separator needs to be added because otherwise the added items 
		// appear after the dropdown instead of before (Eclipse bug?)
		toolBarManager.add(new Separator("dummy")); 
		toolBarManager.add(action);
	}
	
	public void setStatusMessage(String message) {
		setContentDescription(message);
	}
	
}