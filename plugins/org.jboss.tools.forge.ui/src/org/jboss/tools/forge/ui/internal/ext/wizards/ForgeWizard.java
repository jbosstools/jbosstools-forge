/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.tools.forge.ui.internal.ext.wizards;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.widgets.Shell;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.controller.WizardCommandController;
import org.jboss.forge.addon.ui.result.CompositeResult;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.tools.forge.ui.internal.ForgeUIPlugin;
import org.jboss.tools.forge.ui.internal.ext.context.UIContextImpl;
import org.jboss.tools.forge.ui.notifications.NotificationType;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ForgeWizard extends MutableWizard {

	private final CommandController controller;
	private final UIContextImpl uiContext;
	private ForgeWizardHelper helper = new ForgeWizardHelper();

	public ForgeWizard(String windowTitle, CommandController controller,
			UIContextImpl contextImpl) {
		this.controller = controller;
		this.uiContext = contextImpl;
		setWindowTitle(windowTitle);
		setNeedsProgressMonitor(true);
		setForcePreviousAndNextButtons(isWizard());
		helper.onCreate(contextImpl);
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
		return controller.canExecute();
	}

	@Override
	public boolean performFinish() {
		try {
			IWizardContainer container = getContainer();
			performFinish(container, container.getShell());
		} catch (Exception e) {
			ForgeUIPlugin.log(e);
		}
		return true;
	}

	public void performFinish(final IRunnableContext container,
			final Shell shell) throws InvocationTargetException,
			InterruptedException {
		container.run(true, true, new IRunnableWithProgress() {
			@Override
			public void run(IProgressMonitor monitor)
					throws InvocationTargetException, InterruptedException {
				try {
					monitor.beginTask(
							"Executing Forge Wizard", 
							IProgressMonitor.UNKNOWN);
					Map<Object, Object> attributeMap = uiContext
							.getAttributeMap();
					attributeMap.put(IProgressMonitor.class, monitor);
					attributeMap.put(Shell.class, shell);

					Result commandResult = controller.execute();
					displayResult(commandResult);
					helper.onFinish(getUIContext());
					monitor.done();
				} catch (Exception e) {
					ForgeUIPlugin.displayMessage(getWindowTitle(),
							"Error while executing task, check Error log view",
							NotificationType.ERROR);
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
	}

	private void displayResult(Result result) {
		if (result instanceof CompositeResult) {
			for (Result thisResult : ((CompositeResult) result).getResults()) {
				displayResult(thisResult);
			}
		} else if (result != null) {
			String message = result.getMessage();
			if (message != null) {
				NotificationType notificationType = result instanceof Failed ? NotificationType.ERROR
						: NotificationType.INFO;
				ForgeUIPlugin.displayMessage(getWindowTitle(), message,
						notificationType);
			}
			if (result instanceof Failed) {
				Throwable exception = ((Failed) result).getException();
				if (exception != null) {
					ForgeUIPlugin.log(exception);
					ForgeUIPlugin.displayMessage(getWindowTitle(),
							String.valueOf(exception.getMessage()),
							NotificationType.ERROR);
				}
			}
		}
	}

	@Override
	public boolean performCancel() {
		helper.onCancel(getUIContext());
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
		if (nextPage != null) {
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
