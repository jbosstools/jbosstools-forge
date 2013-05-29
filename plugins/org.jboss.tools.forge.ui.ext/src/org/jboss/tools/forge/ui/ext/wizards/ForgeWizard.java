package org.jboss.tools.forge.ui.ext.wizards;

import java.util.List;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.widgets.Display;
import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.wizard.UIWizard;
import org.jboss.forge.proxy.Proxies;
import org.jboss.tools.forge.ext.core.FurnaceService;
import org.jboss.tools.forge.ui.ext.ForgeUIPlugin;
import org.jboss.tools.forge.ui.ext.context.UIContextImpl;
import org.jboss.tools.forge.ui.ext.listeners.EventBus;
import org.jboss.tools.forge.ui.notifications.NotificationDialog;
import org.jboss.tools.forge.ui.notifications.NotificationType;

/**
 * A wizard implementation to handle {@link UICommand} objects
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */
public class ForgeWizard extends MutableWizard {

	private UICommand initialCommand;
	private UIContextImpl uiContext;

	public ForgeWizard(UICommand uiCommand, UIContextImpl context) {
		this.initialCommand = uiCommand;
		this.uiContext = context;
		setNeedsProgressMonitor(true);
		boolean isWizard = uiCommand instanceof UIWizard;
		setForcePreviousAndNextButtons(isWizard);
	}

	@Override
	public void addPages() {
		addPage(new ForgeWizardPage(this, initialCommand, uiContext));
	}

	@Override
	public IWizardPage getNextPage(final IWizardPage page) {
		final ForgeWizardPage currentWizardPage = (ForgeWizardPage) page;
		UICommand uiCommand = currentWizardPage.getUICommand();
		// If it's not a wizard, we don't care
		if (!(uiCommand instanceof UIWizard)) {
			return null;
		}
		UIWizard wiz = (UIWizard) uiCommand;
		NavigationResult nextCommand = null;
		try {
			nextCommand = wiz.next(getUIContext());
		} catch (Exception e) {
			ForgeUIPlugin.log(e);
		}
		// No next page
		if (nextCommand == null) {
			// Clear any subsequent pages that may exist (occurs when navigation
			// changes)
			List<ForgeWizardPage> pageList = getPageList();
			int idx = pageList.indexOf(page) + 1;
			clearNextPagesFrom(idx);
			return null;
		} else {
			Class<? extends UICommand> successor = nextCommand.getNext();
			// Do we have any pages already displayed ? (Did we went back
			// already ?) or did we change anything in the current wizard ?
			// If yes, clear subsequent pages
			ForgeWizardPage nextPage = (ForgeWizardPage) super
					.getNextPage(page);
			if (nextPage == null
					|| (!isNextPageAssignableFrom(nextPage, successor) || currentWizardPage
							.isChanged())) {
				if (nextPage != null) {
					List<ForgeWizardPage> pageList = getPageList();
					int idx = pageList.indexOf(nextPage);
					// Clear the old pages
					clearNextPagesFrom(idx);
				}
				UICommand nextStep = FurnaceService.INSTANCE.lookup(successor);
				nextPage = new ForgeWizardPage(this, nextStep, getUIContext());
				addPage(nextPage);
			}
			return nextPage;
		}
	}

	/**
	 * Clears the next pages from a specific index to the end of the list
	 */
	private void clearNextPagesFrom(int indexFrom) {
		List<ForgeWizardPage> pageList = getPageList();
		pageList.subList(indexFrom, pageList.size()).clear();
	}

	private boolean isNextPageAssignableFrom(ForgeWizardPage nextPage,
			Class<? extends UICommand> successor) {
		return Proxies.isInstance(successor, nextPage.getUICommand());
	}

	@Override
	public boolean performFinish() {
		try {
			for (IWizardPage wizardPage : getPages()) {
				UICommand cmd = ((ForgeWizardPage) wizardPage).getUICommand();
				Result result = cmd.execute(uiContext);
				if (result != null) {
					String message = result.getMessage();
					if (message != null) {
						displayMessage("Forge Command", message,
								NotificationType.INFO);
					}
					if (result instanceof Failed) {
						Throwable exception = ((Failed) result).getException();
						if (exception != null) {
							ForgeUIPlugin.log(exception);
							displayMessage("Forge Command",
									String.valueOf(exception.getMessage()),
									NotificationType.ERROR);
						}
					}
				}
			}
			EventBus.INSTANCE.fireWizardFinished(uiContext);
			return true;
		} catch (Exception e) {
			ForgeUIPlugin.log(e);
			displayMessage("Forge Command", String.valueOf(e.getMessage()),
					NotificationType.ERROR);
			return false;
		}
	}

	protected void displayMessage(final String title, final String message,
			final NotificationType type) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				NotificationDialog.notify(title, message, type);
			}
		});
	}

	@Override
	public boolean performCancel() {
		EventBus.INSTANCE.fireWizardClosed(uiContext);
		return true;
	}

	protected UIContextImpl getUIContext() {
		return uiContext;
	}
}
