/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.forge.ui.ext.wizards;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.IWizardPage;
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
import org.jboss.tools.forge.ui.ext.context.UIExecutionContextImpl;
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
		ForgeWizardPage originalNextPage = (ForgeWizardPage) super
				.getNextPage(currentWizardPage);

		if (!currentWizardPage.isChanged() && originalNextPage != null) {
			result = originalNextPage;
		} else {
			Class<? extends UICommand>[] successors = null;
			try {
				// If it's a wizard, we drill down and get the next page of the
				// wizard
				if (uiCommand instanceof UIWizard) {
					UIWizard wiz = (UIWizard) uiCommand;
					NavigationResult nav = wiz.next(uiContext);
					successors = (nav == null) ? null : nav.getNext();
				}
			} catch (Exception e) {
				ForgeUIPlugin.log(e);
			}
			int pageIndex = invalidateNextPage(currentWizardPage, successors);

			// No next page
			if (successors == null) {
				if (subflows.isEmpty()) {
					result = originalNextPage;
				} else {
					result = createWizardPage(subflows.pop(), true);
					getPageList().add(pageIndex, result);
				}
			} else {
				result = createWizardPage(successors[0], false);
				getPageList().add(pageIndex, result);
				for (int i = 1; i < successors.length; i++) {
					if (successors[i] != null) {
						subflows.add(successors[i]);
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
			final Class<? extends UICommand> successor, boolean subflow) {
		UICommand nextStep = FurnaceService.INSTANCE.lookup(successor);
		return new ForgeWizardPage(this, nextStep, uiContext, subflow);
	}

	@Override
	public boolean performFinish() {
		try {
			getContainer().run(true, true, new IRunnableWithProgress() {
				@Override
				public void run(IProgressMonitor monitor)
						throws InvocationTargetException, InterruptedException {
					UICommand currentCommand = initialCommand;
					UIExecutionContextImpl executionContextImpl = new UIExecutionContextImpl(
							uiContext, monitor);
					try {
						monitor.beginTask("Executing wizard pages",
								getPageCount());
						for (IWizardPage wizardPage : getPages()) {
							if (monitor.isCanceled())
								break;
							currentCommand = ((ForgeWizardPage) wizardPage)
									.getUICommand();
							ForgeUIProvider.INSTANCE.firePreCommandExecuted(
									currentCommand, executionContextImpl);
							CommandExecutor executor = new CommandExecutor(
									currentCommand, executionContextImpl);
							Thread executorThread = new Thread(executor);
							executorThread.start();
							while (executorThread.isAlive()) {
								if (monitor.isCanceled()) {
									executorThread.interrupt();
									break;
								} else {
									Thread.sleep(100);
								}
							}
							ForgeUIProvider.INSTANCE.firePostCommandExecuted(
									currentCommand, executionContextImpl,
									executor.result);
							if (executor.exception != null) {
								throw executor.exception;
							}
							if (executor.result != null) {
								String message = executor.result.getMessage();
								if (message != null) {
									ForgeUIPlugin.displayMessage(
											"Forge Command", message,
											NotificationType.INFO);
								}
								if (executor.result instanceof Failed) {
									Throwable exception = ((Failed) executor.result)
											.getException();
									if (exception != null) {
										ForgeUIPlugin.log(exception);
										ForgeUIPlugin.displayMessage(
												"Forge Command", String
														.valueOf(exception
																.getMessage()),
												NotificationType.ERROR);
									}
								}
							}
							monitor.worked(1);
						}
						EventBus.INSTANCE.fireWizardFinished(uiContext);
					} catch (Exception ex) {
						ForgeUIProvider.INSTANCE.firePostCommandFailure(
								currentCommand, executionContextImpl, ex);
						ForgeUIPlugin.log(ex);
						ForgeUIPlugin.displayMessage("Forge Command",
								String.valueOf(ex.getMessage()),
								NotificationType.ERROR);
					} finally {
						uiContext.destroy();
						monitor.done();
					}
				}
			});
		} catch (Exception e) {
			ForgeUIPlugin.log(e);
		}
		return true;
	}

	@Override
	public boolean performCancel() {
		EventBus.INSTANCE.fireWizardClosed(uiContext);
		return true;
	}

	private class CommandExecutor implements Runnable {

		private final UICommand command;
		private final UIExecutionContextImpl executionContextImpl;
		Result result;
		Exception exception;

		CommandExecutor(UICommand command,
				UIExecutionContextImpl executionContext) {
			this.command = command;
			this.executionContextImpl = executionContext;
		}

		@Override
		public void run() {
			try {
				result = command.execute(executionContextImpl);
			} catch (Exception e) {
				exception = e;
			}
		}

	}

}
