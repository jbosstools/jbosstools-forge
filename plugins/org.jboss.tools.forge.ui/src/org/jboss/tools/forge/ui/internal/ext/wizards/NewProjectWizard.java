/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.tools.forge.ui.internal.ext.wizards;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.jboss.tools.forge.core.furnace.FurnaceRuntime;
import org.jboss.tools.forge.core.runtime.ForgeRuntimeState;
import org.jboss.tools.forge.ui.internal.ext.dialog.UICommandListDialog;
import org.jboss.tools.forge.ui.internal.ext.dialog.WizardDialogHelper;
import org.jboss.tools.forge.ui.util.ForgeHelper;

/**
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class NewProjectWizard implements INewWizard {

	private IWorkbench workbench;
	private IStructuredSelection selection;

	private ForgeWizard delegate;

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.workbench = workbench;
		this.selection = selection;
	}

	private ForgeWizard getDelegate() {
		if (delegate == null) {
			if (!ForgeRuntimeState.RUNNING.equals(FurnaceRuntime.INSTANCE.getState())) {
				ForgeHelper.start(FurnaceRuntime.INSTANCE);
			}
			String commandName = "Project: New";
			IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
			ITextSelection textSelection = UICommandListDialog.getTextSelection(workbenchWindow);
			WizardDialogHelper helper = new WizardDialogHelper(workbench.getDisplay().getActiveShell(), selection,
					textSelection);
			this.delegate = helper.createForgeWizard(commandName, commandName);
		}
		return delegate;
	}

	@Override
	public void addPages() {
		getDelegate().addPages();
	}

	@Override
	public boolean canFinish() {
		return getDelegate().canFinish();
	}

	@Override
	public void createPageControls(Composite pageContainer) {
		getDelegate().createPageControls(pageContainer);
	}

	@Override
	public void dispose() {
		getDelegate().dispose();
	}

	@Override
	public IWizardContainer getContainer() {
		return getDelegate().getContainer();
	}

	@Override
	public Image getDefaultPageImage() {
		return getDelegate().getDefaultPageImage();
	}

	@Override
	public IDialogSettings getDialogSettings() {
		return getDelegate().getDialogSettings();
	}

	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		return getDelegate().getNextPage(page);
	}

	@Override
	public IWizardPage getPage(String pageName) {
		return getDelegate().getPage(pageName);
	}

	@Override
	public int getPageCount() {
		return getDelegate().getPageCount();
	}

	@Override
	public IWizardPage[] getPages() {
		return getDelegate().getPages();
	}

	@Override
	public IWizardPage getPreviousPage(IWizardPage page) {
		return getDelegate().getPreviousPage(page);
	}

	@Override
	public IWizardPage getStartingPage() {
		return getDelegate().getStartingPage();
	}

	@Override
	public RGB getTitleBarColor() {
		return getDelegate().getTitleBarColor();
	}

	@Override
	public String getWindowTitle() {
		return getDelegate().getWindowTitle();
	}

	@Override
	public boolean isHelpAvailable() {
		return getDelegate().isHelpAvailable();
	}

	@Override
	public boolean needsPreviousAndNextButtons() {
		return getDelegate().needsPreviousAndNextButtons();
	}

	@Override
	public boolean needsProgressMonitor() {
		return getDelegate().needsProgressMonitor();
	}

	@Override
	public boolean performCancel() {
		return getDelegate().performCancel();
	}

	@Override
	public boolean performFinish() {
		return getDelegate().performFinish();
	}

	@Override
	public void setContainer(IWizardContainer wizardContainer) {
		getDelegate().setContainer(wizardContainer);
	}
}
