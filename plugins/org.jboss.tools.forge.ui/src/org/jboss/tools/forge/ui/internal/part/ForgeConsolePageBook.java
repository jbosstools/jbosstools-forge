package org.jboss.tools.forge.ui.internal.part;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.part.PageBook;
import org.jboss.tools.forge.ui.internal.console.ForgeConsole;
import org.jboss.tools.forge.ui.internal.console.ForgeConsoleManager;

public class ForgeConsolePageBook extends PageBook {
	
	private ForgeConsoleView forgeConsoleView = null;
	private ForgeConsolePage currentPage = null;
	private Map<ForgeConsole, ForgeConsolePage> forgeConsoleToPage = 
			new HashMap<ForgeConsole, ForgeConsolePage>();

	public ForgeConsolePageBook(ForgeConsoleView forgeConsoleView, Composite parent) {
		super(parent, SWT.NONE);
		this.forgeConsoleView = forgeConsoleView;
		initializePages();
	}
	
	private void initializePages() {
		for (ForgeConsole forgeConsole : ForgeConsoleManager.INSTANCE.getConsoles()) {
			ForgeConsolePage forgeConsolePage = new ForgeConsolePage(this, forgeConsole);
			forgeConsolePage.initialize(getViewSite());
			forgeConsolePage.createControl();
			forgeConsoleToPage.put(forgeConsole, forgeConsolePage);
		}		
	}
	
	private IViewSite getViewSite() {
		return forgeConsoleView.getViewSite();
	}

	public void showForgeConsole(ForgeConsole forgeConsole) {
		if (currentPage != null) {
			currentPage.deactivateActionBars();
		}
		ForgeConsolePage page = forgeConsoleToPage.get(forgeConsole);
		if (page != null) {
			page.show();
			page.activateActionBars();
			currentPage = page;
		}
		getViewSite().getActionBars().updateActionBars();
	}
	
	void updateStatusMessage(String message) {
		forgeConsoleView.setStatusMessage(message);
	}
	
	ForgeConsolePage getCurrentPage() {
		return currentPage;
	}
	
}
