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
	private MessagePage messagePage = null;
	private Control messagePageControl = null;
	
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
	
	public void setMessage(String message) {
		if (messagePage != null) {
			messagePage.setMessage(message);
		}
	}
	
	private void createPageBook() {
		pageBook = new PageBook(parent, SWT.NONE);
	}
	
	private void createMessagePage() {
		messagePage = new MessagePage();
		messagePage.createControl(pageBook);
		messagePage.init(new PageSite(getViewSite()));
		messagePage.setMessage("Forge Console View");
		messagePageControl = messagePage.getControl();
	}
	
	private void showMessagePage() {
		pageBook.showPage(messagePageControl);
	}
	
	private void createActions() {
		ForgeConsoleDropdownAction action = new ForgeConsoleDropdownAction(this);
		IToolBarManager toolBarManager = getViewSite().getActionBars().getToolBarManager();
		toolBarManager.add(action);
		getViewSite().getActionBars().updateActionBars();
	}

}