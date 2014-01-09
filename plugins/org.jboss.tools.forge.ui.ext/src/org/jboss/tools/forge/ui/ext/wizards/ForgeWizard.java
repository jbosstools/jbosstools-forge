/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.tools.forge.ui.ext.wizards;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.widgets.Shell;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.controller.WizardCommandController;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.tools.forge.ui.ext.ForgeUIPlugin;
import org.jboss.tools.forge.ui.ext.context.UIContextImpl;
import org.jboss.tools.forge.ui.ext.listeners.EventBus;
import org.jboss.tools.forge.ui.notifications.NotificationType;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ForgeWizard extends MutableWizard {

	private final CommandController controller;
	private final UIContextImpl uiContext;

	public ForgeWizard(CommandController controller, UIContextImpl contextImpl) {
		this.controller = controller;
		this.uiContext = contextImpl;
		setNeedsProgressMonitor(true);
		setForcePreviousAndNextButtons(isWizard());
	}

	public UIContextImpl getUIContext() {
		return uiContext;
	}

	@Override
	public void addPages() {
		addPage(createPage());
	}

	@Override
	public boolean canFinish() {
		if (isWizard()) {
			return ((WizardCommandController) controller).canExecute();
		} else {
			return controller.isValid();
		}
	}

	@Override
	public boolean performFinish() {
		try {
			final IWizardContainer container = getContainer();
			// Cannot fork, otherwise Eclipse Shell in UIPrompt will throw an
			// exception
			boolean fork = false;
			container.run(fork, true, new IRunnableWithProgress() {
				@Override
				public void run(IProgressMonitor monitor)
						throws InvocationTargetException, InterruptedException {
					try {
						Map<Object, Object> attributeMap = uiContext
								.getAttributeMap();
						attributeMap.put(IProgressMonitor.class, monitor);
						attributeMap.put(Shell.class, container.getShell());

						Result result = controller.execute();
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
									ForgeUIPlugin.displayMessage(
											"Forge Command", String
													.valueOf(exception
															.getMessage()),
											NotificationType.ERROR);
								}
							}
							EventBus.INSTANCE.fireWizardFinished(uiContext);
						}
					} catch (Exception e) {
						ForgeUIPlugin.log(e);
					} finally {
						try {
							controller.close();
						} catch (Exception e) {
							ForgeUIPlugin.log(e);
						}
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

	protected ForgeWizardPage createPage() {
		return new ForgeWizardPage(this, controller);
	}

	private boolean isWizard() {
		return controller instanceof WizardCommandController;
	}

	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		IWizardPage nextPage = super.getNextPage(page);
		if (nextPage != null && !controller.isInitialized()) {
			// Subsequent pages are stale. Remove all subsequent pages
			removeSubsequentPages(nextPage);
			nextPage = null;
		}
		if (nextPage == null) {
			try {
				addPage(createPage());
				nextPage = super.getNextPage(page);
			} catch (Exception e) {
				ForgeUIPlugin.log(e);
			}
		}
		return nextPage;
	}

	/**
	 * @param page
	 */
	private void removeSubsequentPages(IWizardPage page) {
		List<ForgeWizardPage> pageList = getPageList();
		int idx = pageList.indexOf(page);
		List<ForgeWizardPage> subList = pageList.subList(idx, pageList.size());
		for (ForgeWizardPage forgeWizardPage : subList) {
			forgeWizardPage.dispose();
		}
		subList.clear();
	}
}
