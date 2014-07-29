package org.jboss.tools.forge.ui.internal.part;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.IShowInTarget;
import org.eclipse.ui.part.ShowInContext;
import org.eclipse.ui.part.ViewPart;
import org.jboss.tools.forge.ui.internal.actions.ForgeConsoleDropdownAction;
import org.jboss.tools.forge.ui.internal.actions.GoToAction;
import org.jboss.tools.forge.ui.internal.console.ForgeConsole;
import org.jboss.tools.forge.ui.internal.console.ForgeConsoleManager;

public class ForgeConsoleView extends ViewPart implements IShowInTarget {

	public static final String ID = "org.jboss.tools.forge.ui.console";
	public static final String FORGE_CONSOLE_ACTION_GROUP = "org.jboss.tools.forge.ui.console.actions";

	private Composite parent = null;
	private ForgeConsolePageBook forgeConsolePageBook = null;
	private ForgeConsole current = null;

	@Override
	public void createPartControl(Composite parent) {
		this.parent = parent;
		createActions();
		createPageBook();
		showForgeConsole(ForgeConsoleManager.INSTANCE.getDefaultConsole());
	}

	@Override
	public void setFocus() {
		// nothing to do (yet)
	}

	public void showForgeConsole(ForgeConsole forgeConsole) {
		current = forgeConsole;
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
		IToolBarManager toolBarManager = getViewSite().getActionBars()
				.getToolBarManager();
		toolBarManager.add(new Separator(FORGE_CONSOLE_ACTION_GROUP));
		// additional separator needs to be added because otherwise the added
		// items
		// appear after the dropdown instead of before (Eclipse bug?)
		toolBarManager.add(new Separator("dummy"));
		toolBarManager.add(action);
	}

	public void setStatusMessage(String message) {
		setContentDescription(message);
	}

	/**
	 * Shows the given context in the Forge Console. The target should check the
	 * context's selection for elements to show. If there are no relevant
	 * elements in the selection, then it should check the context's input.
	 *
	 * @param context
	 *            the context to show
	 * @return <code>true</code> if the context could be shown,
	 *         <code>false</code> otherwise
	 * @see org.eclipse.ui.part.IShowInTarget#show(org.eclipse.ui.part.ShowInContext)
	 */
	@Override
	public boolean show(ShowInContext context) {
		if (current != null) {
			try (GoToAction action = new GoToAction(current.getRuntime())) {
				action.selectionChanged(this, context.getSelection());
				return action.goToSelection();
			}
		}
		return false;
	}
}