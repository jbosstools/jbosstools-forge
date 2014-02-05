package org.jboss.tools.forge.ui.part;

import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.SubActionBars;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.part.PageSite;
import org.jboss.tools.forge.core.process.ForgeRuntime;
import org.jboss.tools.forge.ui.console.ForgeConsole;

public class ForgeConsolePage implements IPage {
	
	private ForgeConsolePageBook forgeConsolePageBook = null;
	private ForgeConsole forgeConsole = null;
	private Control control = null;
	private IPageSite pageSite = null;
	private SubActionBars actionBars = null;
	
	public ForgeConsolePage(ForgeConsolePageBook forgeConsolePageBook, ForgeConsole forgeConsole) {
		this.forgeConsolePageBook = forgeConsolePageBook;
		this.forgeConsole = forgeConsole;
	}
	
	public void createControl() {
		createControl(forgeConsolePageBook);
	}
	
	@Override 
	public void createControl(Composite parent) {
		control = forgeConsole.createControl(parent);
	}

	@Override
	public Control getControl() {
		return control;
	}

	@Override
	public void setFocus() {
		if (control != null) {
			control.setFocus();
		}
	}
	
	@Override
	public void dispose() {
        Control control = getControl();
        if (control != null && !control.isDisposed()) {
			control.dispose();
		}
        actionBars.dispose();
	}

	@Override
	public void setActionBars(IActionBars actionBars) {
	}
	
	void initialize(IViewSite viewSite) {
		pageSite = new PageSite(viewSite);
		actionBars = (SubActionBars)pageSite.getActionBars();
		IAction[] actions = forgeConsole.createActions();
		for (IAction action : actions) {
			actionBars.getToolBarManager().appendToGroup(
					ForgeConsoleView.FORGE_CONSOLE_ACTION_GROUP, 
					action);;
		}
	}
	
	void activateActionBars() {
		actionBars.activate();
	}
	
	void deactivateActionBars() {
		actionBars.deactivate();
	}
	
	void show() {
		forgeConsolePageBook.showPage(getControl());
		forgeConsolePageBook.updateStatusMessage(getStatusMessage());
	}
	
	String getStatusMessage() {
		ForgeRuntime runtime = forgeConsole.getRuntime();
		return "Forge " + runtime.getName() + " @ " + runtime.getLocation();
	}
	
}
