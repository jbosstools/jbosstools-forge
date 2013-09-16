/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.forge.ui.ext.wizards;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.m2e.core.MavenPlugin;
import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.wizard.UIWizard;
import org.jboss.forge.furnace.proxy.Proxies;
import org.jboss.tools.forge.ext.core.FurnaceService;
import org.jboss.tools.forge.ui.ext.ForgeUIPlugin;
import org.jboss.tools.forge.ui.ext.ForgeUIProvider;
import org.jboss.tools.forge.ui.ext.context.UIContextImpl;
import org.jboss.tools.forge.ui.ext.listeners.EventBus;
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

	private LinkedList<Class<? extends UICommand>> subflows = new LinkedList<Class<? extends UICommand>>();

	public ForgeWizard(UICommand uiCommand, UIContextImpl context) {
		this.initialCommand = uiCommand;
		this.uiContext = context;
		setNeedsProgressMonitor(true);
		boolean isWizard = uiCommand instanceof UIWizard;
		setForcePreviousAndNextButtons(isWizard);
	}

	@Override
	public void addPages() {
		addPage(new ForgeWizardPage(this, initialCommand, uiContext, false));
	}

	@Override
	public ForgeWizardPage getNextPage(final IWizardPage page) {
		final ForgeWizardPage result;
		final ForgeWizardPage currentWizardPage = (ForgeWizardPage) page;
		UICommand uiCommand = currentWizardPage.getUICommand();
		// If it's not a wizard, we don't care
		if (!(uiCommand instanceof UIWizard)) {
			return null;
		}
		UIWizard wiz = (UIWizard) uiCommand;
		ForgeWizardPage originalNextPage = (ForgeWizardPage) super
				.getNextPage(currentWizardPage);

		if (!currentWizardPage.isChanged() && originalNextPage != null) {
			result = originalNextPage;
		} else {
			Class<? extends UICommand>[] successors = null;
			try {
				NavigationResult nav = wiz.next(getUIContext());
				successors = (nav == null) ? null : nav.getNext();
			} catch (Exception e) {
				ForgeUIPlugin.log(e);
			}
			int pageIndex = invalidateNextPage(currentWizardPage, successors);

			// No next page
			if (successors == null) {
				if (subflows.isEmpty()) {
					result = originalNextPage;
				} else {
					result = createWizardPage(subflows.pop(), pageIndex, true);
				}
			} else {
				result = createWizardPage(successors[0], pageIndex, false);
				for (int i = 1; i < successors.length; i++) {
					if (successors[i] != null) {
						subflows.push(successors[i]);
					}
				}
			}
		}
		currentWizardPage.setChanged(false);
		return result;
	}

	/**
	 * Invalidates the pages after the original page
	 */
	private int invalidateNextPage(ForgeWizardPage currentWizardPage,
			Class<? extends UICommand>[] successors) {
		ForgeWizardPage originalNextPage = (ForgeWizardPage) super
				.getNextPage(currentWizardPage);
		List<ForgeWizardPage> pageList = getPageList();
		int idx = getPageCount();
		if (originalNextPage != null) {
			idx = clearNextPagesFrom(originalNextPage);
			if (successors != null) {
				for (Class<? extends UICommand> successor : successors) {
					for (int i = pageList.size() - 1; i > -1; i--) {
						if (Proxies.isInstance(successor, pageList.get(i)
								.getUICommand())) {
							pageList.remove(i);
						}
					}
					subflows.remove(successor);
				}
			}
		}
		return idx;
	}

	/**
	 * Clears the next pages from a specific page to the end of the list
	 * 
	 * Returns the position where the next page should be added
	 */
	private int clearNextPagesFrom(final IWizardPage page) {
		List<ForgeWizardPage> pageList = getPageList();
		int idx = pageList.indexOf(page);
		int finalIdx = pageList.size();
		for (int i = idx; i < finalIdx; i++) {
			if (pageList.get(i).isSubflowHead()) {
				finalIdx = i;
				break;
			}
		}
		pageList.subList(idx, finalIdx).clear();
		return idx;
	}

	private ForgeWizardPage createWizardPage(
			final Class<? extends UICommand> successor, int index,
			boolean subflow) {
		ForgeWizardPage nextPage;
		UICommand nextStep = FurnaceService.INSTANCE.lookup(successor);
		nextPage = new ForgeWizardPage(this, nextStep, getUIContext(), subflow);
		nextPage.setWizard(this);
		getPageList().add(index, nextPage);
		return nextPage;
	}

	@Override
	public boolean performFinish() {
		FinishJob finishJob = new FinishJob("Finishing '"
				+ initialCommand.getMetadata().getName() + "'");
		finishJob.schedule();
		return true;
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
			UICommand currentCommand = initialCommand;
			try {
				monitor.beginTask("Executing wizard pages", getPageCount());
				for (IWizardPage wizardPage : getPages()) {
					currentCommand = ((ForgeWizardPage) wizardPage)
							.getUICommand();
					ForgeUIProvider.INSTANCE.firePreCommandExecuted(
							currentCommand, uiContext);
					Result result = currentCommand.execute(uiContext);
					ForgeUIProvider.INSTANCE.firePostCommandExecuted(
							currentCommand, uiContext, result);
					if (result != null) {
						String message = result.getMessage();
						if (message != null) {
							ForgeUIPlugin.displayMessage("Forge Command",
									message, NotificationType.INFO);
						}
						if (result instanceof Failed) {
							Throwable exception = ((Failed) result)
									.getException();
							if (exception != null) {
								ForgeUIPlugin.log(exception);
								ForgeUIPlugin.displayMessage("Forge Command",
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
				ForgeUIProvider.INSTANCE.firePostCommandFailure(currentCommand,
						uiContext, ex);
				ForgeUIPlugin.log(ex);
				ForgeUIPlugin
						.displayMessage("Forge Command",
								String.valueOf(ex.getMessage()),
								NotificationType.ERROR);
				return Status.CANCEL_STATUS;
			} finally {
				monitor.done();
			}
		}
	}
}
