/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.tools.forge.ui.ext.dialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.IPageChangingListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.dialogs.PageChangingEvent;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.wizard.UIWizardStep;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.services.Imported;
import org.jboss.tools.forge.ext.core.FurnaceService;
import org.jboss.tools.forge.ui.ext.ForgeUIPlugin;
import org.jboss.tools.forge.ui.ext.context.UIContextImpl;
import org.jboss.tools.forge.ui.ext.wizards.ForgeWizard;
import org.jboss.tools.forge.ui.ext.wizards.ForgeWizardPage;

/**
 */
public final class WizardDialogHelper {

	private UIContextImpl context;
	private Shell parentShell;

	public WizardDialogHelper(Shell parentShell, IStructuredSelection selection) {
		this.parentShell = parentShell;
		context = new UIContextImpl(selection);
	}

	public WizardDialogHelper(Shell parentShell, UIContextImpl context) {
		this.parentShell = parentShell;
		this.context = context;
	}

	public UIContextImpl getContext() {
		return context;
	}

	public List<UICommand> getAllCandidatesAsList() {
		List<UICommand> result = new ArrayList<UICommand>();
		AddonRegistry addonRegistry = FurnaceService.INSTANCE
				.getAddonRegistry();
		Imported<UICommand> instances = addonRegistry
				.getServices(UICommand.class);
		for (UICommand uiCommand : instances) {
			try {
				if (!(uiCommand instanceof UIWizardStep)
						&& uiCommand.isEnabled(context)) {
					result.add(uiCommand);
				}
			} catch (Exception e) {
				ForgeUIPlugin.log(e);
			}
		}
		return result;
	}

	public Map<String, UICommand> getAllCandidatesAsMap() {
		Map<String, UICommand> result = new TreeMap<String, UICommand>();
		AddonRegistry addonRegistry = FurnaceService.INSTANCE
				.getAddonRegistry();
		Imported<UICommand> instances = addonRegistry
				.getServices(UICommand.class);
		for (UICommand uiCommand : instances) {
			try {
				if (!(uiCommand instanceof UIWizardStep)
						&& uiCommand.isEnabled(context)) {
					UICommandMetadata metadata = uiCommand.getMetadata();
					result.put(metadata.getName(), uiCommand);
				}
			} catch (Exception e) {
				ForgeUIPlugin.log(e);
			}

		}
		return result;
	}

	public void openWizard(String windowTitle, UICommand selectedCommand) {
		ForgeWizard wizard = new ForgeWizard(selectedCommand, context);
		wizard.setWindowTitle(windowTitle);
		final WizardDialog wizardDialog = new WizardDialog(parentShell, wizard);
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
