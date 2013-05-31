/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.tools.forge.ui.ext.dialog;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.IPageChangingListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.dialogs.PageChangingEvent;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchWindow;
import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.wizard.UIWizardStep;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.services.ExportedInstance;
import org.jboss.tools.forge.ext.core.FurnaceService;
import org.jboss.tools.forge.ui.ext.context.UIContextImpl;
import org.jboss.tools.forge.ui.ext.wizards.ForgeWizard;
import org.jboss.tools.forge.ui.ext.wizards.ForgeWizardPage;

public abstract class AbstractUICommandDialog extends PopupDialog {
	protected final UIContextImpl uiContext;

	public AbstractUICommandDialog(IWorkbenchWindow window) {
		super(window.getShell(), SWT.RESIZE, true,
				true, // persist size
				false, // but not location
				true, true, "Run a Forge command",
				"Start typing to filter the list");
		ISelection selection = window.getSelectionService().getSelection();
		IStructuredSelection currentSelection = null;
		if (selection instanceof IStructuredSelection) {
			currentSelection = (IStructuredSelection) selection;
		}
		uiContext = UIContextImpl.createContext(currentSelection);
	}

	protected Map<String, UICommand> getAllCandidatesAsMap() {
		Map<String, UICommand> result = new TreeMap<String, UICommand>();
		AddonRegistry addonRegistry = FurnaceService.INSTANCE
				.getAddonRegistry();
		Set<ExportedInstance<UICommand>> exportedInstances = addonRegistry
				.getExportedInstances(UICommand.class);
		for (ExportedInstance<UICommand> instance : exportedInstances) {
			UICommand uiCommand = instance.get();
			if (!(uiCommand instanceof UIWizardStep)
					&& uiCommand.isEnabled(uiContext)) {
				UICommandMetadata metadata = uiCommand.getMetadata();
				result.put(metadata.getName(), uiCommand);
			}
		}
		return result;
	}

	protected void openWizard(String windowTitle, UICommand selectedCommand) {
		ForgeWizard wizard = new ForgeWizard(selectedCommand, uiContext);
		wizard.setWindowTitle(windowTitle);
		final WizardDialog wizardDialog = new WizardDialog(getParentShell(),
				wizard);
		// TODO: Show help button when it's possible to display the docs for
		// each UICommand
		wizardDialog.setHelpAvailable(false);
		wizardDialog.addPageChangingListener(new IPageChangingListener() {

			@Override
			public void handlePageChanging(PageChangingEvent event) {
				// Mark as unchanged
				ForgeWizardPage currentPage = (ForgeWizardPage) event
						.getCurrentPage();
				if (currentPage != null) {
					currentPage.setChanged(false);
				}
			}
		});
		wizardDialog.addPageChangedListener(new IPageChangedListener() {
			@Override
			public void pageChanged(PageChangedEvent event) {
				// BEHAVIOR: Finish button is enabled by default and then
				// disabled when any field is selected/changed in the first page

				// WHY: Wizard.canFinish() is called before getNextPage is
				// called, therefore not checking if the second page is complete

				// SOLUTION: Calling updateButtons will call canFinish() once
				// again and set the button to the correct state
				wizardDialog.updateButtons();
			}
		});
		wizardDialog.open();
	}

}
