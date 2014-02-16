package org.jboss.tools.forge.ui.part;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.SubActionBars;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.part.PageSite;
import org.jboss.tools.forge.core.runtime.ForgeRuntime;
import org.jboss.tools.forge.ui.console.ForgeConsole;

public class ForgeConsolePage implements IPage, PropertyChangeListener {
	
	private ForgeConsolePageBook forgeConsolePageBook = null;
	private ForgeConsole forgeConsole = null;
	private Control control = null;
	private IPageSite pageSite = null;
	private SubActionBars actionBars = null;
	
	public ForgeConsolePage(ForgeConsolePageBook forgeConsolePageBook, ForgeConsole forgeConsole) {
		this.forgeConsolePageBook = forgeConsolePageBook;
		this.forgeConsole = forgeConsole;
		forgeConsole.getRuntime().addPropertyChangeListener(this);
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
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if ((forgeConsolePageBook.getCurrentPage() == this) 
				&& ForgeRuntime.PROPERTY_STATE.equals(evt.getPropertyName())) {
			updateActionBars();
		}
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
		return forgeConsole.getLabel();
	}
	
	private void updateActionBars() {
		// run in UI thread
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				// without calls to deactivate and activate the actionBars
				// do not update properly
				actionBars.deactivate();;
				actionBars.updateActionBars();
				actionBars.activate();
				actionBars.updateActionBars();
			}			
		});
	}

}
