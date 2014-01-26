package org.jboss.tools.forge.ui.part;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.part.PageSite;
import org.eclipse.ui.part.ViewPart;
import org.jboss.tools.forge.ui.actions.ForgeConsoleDropdownAction;
import org.jboss.tools.forge.ui.console.ForgeConsole;
import org.jboss.tools.forge.ui.console.ForgeConsoleManager;

public class ForgeConsoleView extends ViewPart {

	private Composite parent = null;
	private PageBook pageBook = null;
	private Map<ForgeConsole, Control> consoleToControl = new HashMap<ForgeConsole, Control>();
	private ForgeConsole current = null;
	
	@Override
	public void createPartControl(Composite parent) {
		this.parent = parent;
		createPageBook();
		createActions();
		setContentDescription("No Forge runtime is currently selected.");
	}

	@Override
	public void setFocus() {
		// nothing to do (yet)
	}
	
	public void showForgeConsole(ForgeConsole forgeConsole) {
		Control control = consoleToControl.get(forgeConsole);
		if (control != null) {
			setContentDescription(forgeConsole.getName());
			pageBook.showPage(control);
			current = forgeConsole;
		}
	}
	
	public ForgeConsole getConsole() {
		return current;
	}
	
	private void createPageBook() {
		pageBook = new PageBook(parent, SWT.NONE);
		for (ForgeConsole forgeConsole : ForgeConsoleManager.INSTANCE.getConsoles()) {
			ForgeConsolePage forgeConsolePage = new ForgeConsolePage(forgeConsole);
			forgeConsolePage.createControl(pageBook);
			consoleToControl.put(forgeConsole, forgeConsolePage.getControl());
			forgeConsolePage.init(new PageSite(getViewSite()));
		}
	}
	
	private void createActions() {
		ForgeConsoleDropdownAction action = new ForgeConsoleDropdownAction(this);
		IToolBarManager toolBarManager = getViewSite().getActionBars().getToolBarManager();
		toolBarManager.add(action);
		getViewSite().getActionBars().updateActionBars();
	}

}