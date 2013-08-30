package org.jboss.tools.forge.ui.ext.wizards;

import java.util.List;
import java.util.Stack;

import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.swt.widgets.Display;
import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.wizard.UIWizard;
import org.jboss.forge.furnace.proxy.Proxies;
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

	private Stack<Class<? extends UICommand>> subflows = new Stack<Class<? extends UICommand>>();

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
			if (!subflows.isEmpty()) {
				Class<? extends UICommand> subflowSuccessor = subflows.pop();
				return createWizardPage(subflowSuccessor);
			}
			return null;
		} else {
			Class<? extends UICommand>[] successors = nextCommand.getNext();
			final Class<? extends UICommand> successor = successors[0];
			for (int i = 1; i < successors.length; i++) {
				subflows.push(successors[i]);
			}
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
				nextPage = createWizardPage(successor);
			}
			return nextPage;
		}
	}

	private ForgeWizardPage createWizardPage(
			final Class<? extends UICommand> successor) {
		ForgeWizardPage nextPage;
		UICommand nextStep = FurnaceService.INSTANCE.lookup(successor);
		nextPage = new ForgeWizardPage(this, nextStep, getUIContext());
		addPage(nextPage);
		return nextPage;
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
		FinishJob finishJob = new FinishJob("Finishing '"
				+ initialCommand.getMetadata().getName() + "'");
		finishJob.schedule();
		return true;
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

	/**
	 * Called when Finish is called
	 * 
	 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
	 * 
	 */
	private class FinishJob extends WorkspaceJob {
		public FinishJob(String name) {
			super(name);
			// TODO: Check if rule is correct
			setRule(MavenPlugin.getProjectConfigurationManager().getRule());
		}

		@Override
		public IStatus runInWorkspace(IProgressMonitor monitor)
				throws CoreException {
			try {
				monitor.beginTask("Executing wizard pages", getPageCount());
				for (IWizardPage wizardPage : getPages()) {
					UICommand cmd = ((ForgeWizardPage) wizardPage)
							.getUICommand();
					Result result = cmd.execute(uiContext);
					if (result != null) {
						String message = result.getMessage();
						if (message != null) {
							displayMessage("Forge Command", message,
									NotificationType.INFO);
						}
						if (result instanceof Failed) {
							Throwable exception = ((Failed) result)
									.getException();
							if (exception != null) {
								ForgeUIPlugin.log(exception);
								displayMessage("Forge Command",
										String.valueOf(exception.getMessage()),
										NotificationType.ERROR);
							}
						}
					}
					monitor.worked(1);
				}
				EventBus.INSTANCE.fireWizardFinished(uiContext);
				return Status.OK_STATUS;
			} catch (Exception ex) {
				ForgeUIPlugin.log(ex);
				displayMessage("Forge Command",
						String.valueOf(ex.getMessage()), NotificationType.ERROR);
				return Status.CANCEL_STATUS;
			} finally {
				monitor.done();
			}
		}
	}
}
