package org.jboss.tools.forge.ui.part;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.part.ViewPart;
import org.jboss.tools.forge.ui.actions.ForgeConsoleDropdownAction;
import org.jboss.tools.forge.ui.console.ForgeConsole;
import org.jboss.tools.forge.ui.console.ForgeConsoleManager;

public class ForgeConsoleView extends ViewPart {
	
	public static final String FORGE_CONSOLE_ACTION_GROUP = "org.jboss.tools.forge.ui.console.actions";

	private Composite parent = null;
	private PageBook pageBook = null;
	private Map<ForgeConsole, ForgeConsolePage> consoleToPage = new HashMap<ForgeConsole, ForgeConsolePage>();
	private ForgeConsole current = null;
	
	@Override
	public void createPartControl(Composite parent) {
		this.parent = parent;
		createActions();
		createPageBook();
		setContentDescription("No Forge runtime is currently selected.");
	}

	@Override
	public void setFocus() {
		// nothing to do (yet)
	}
	
	public void showForgeConsole(ForgeConsole forgeConsole) {
		if (current != null) {
			ForgeConsolePage currentPage = consoleToPage.get(current);
			if (currentPage != null) {
				currentPage.deactivateActionBars();
			}
		}
		ForgeConsolePage page = consoleToPage.get(forgeConsole);
		if (page != null) {
			setContentDescription(forgeConsole.getName());
			pageBook.showPage(page.getControl());
			page.activateActionBars();
			current = forgeConsole;
		}
		getViewSite().getActionBars().updateActionBars();
	}
	
	public ForgeConsole getConsole() {
		return current;
	}
	
	private void createPageBook() {
		pageBook = new PageBook(parent, SWT.NONE);
		for (ForgeConsole forgeConsole : ForgeConsoleManager.INSTANCE.getConsoles()) {
			ForgeConsolePage forgeConsolePage = new ForgeConsolePage(forgeConsole);
			forgeConsolePage.initialize(getViewSite());
			forgeConsolePage.createControl(pageBook);
			consoleToPage.put(forgeConsole, forgeConsolePage);
		}
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
	
}