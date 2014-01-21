package org.jboss.tools.forge.ui.part;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.part.MessagePage;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.part.PageSite;
import org.eclipse.ui.part.ViewPart;

public class ForgeConsoleView extends ViewPart {

	private Composite parent = null;
	private PageBook pageBook = null;
	private Control messagePage = null;
	
	@Override
	public void createPartControl(Composite parent) {
		this.parent = parent;
		createPageBook();
		createMessagePage();
		createActions();
		showMessagePage();
	}

	@Override
	public void setFocus() {
		// nothing to do (yet)
	}
	
	private void createPageBook() {
		pageBook = new PageBook(parent, SWT.NONE);
	}
	
	private void createMessagePage() {
		MessagePage page = new MessagePage();
		page.createControl(pageBook);
		page.init(new PageSite(getViewSite()));
		page.setMessage("Forge Console View");
		messagePage = page.getControl();
	}
	
	private void showMessagePage() {
		pageBook.showPage(messagePage);
	}
	
	private void createActions() {
		ForgeConsoleDropdownAction action = new ForgeConsoleDropdownAction();
		IToolBarManager toolBarManager = getViewSite().getActionBars().getToolBarManager();
		toolBarManager.add(action);
		getViewSite().getActionBars().updateActionBars();
	}

}