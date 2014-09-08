/**
 * Copyright (c) Red Hat, Inc., contributors and others 2013 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.forge.ui.internal.ext.dialog;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.IPageChangingListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.dialogs.PageChangingEvent;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.jboss.forge.addon.ui.command.CommandFactory;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.controller.CommandControllerFactory;
import org.jboss.forge.addon.ui.controller.WizardCommandController;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.wizard.UIWizardStep;
import org.jboss.tools.forge.core.furnace.FurnaceService;
import org.jboss.tools.forge.ui.internal.ForgeUIPlugin;
import org.jboss.tools.forge.ui.internal.ext.context.UIContextImpl;
import org.jboss.tools.forge.ui.internal.ext.provider.ForgeUIProvider;
import org.jboss.tools.forge.ui.internal.ext.provider.ForgeUIRuntime;
import org.jboss.tools.forge.ui.internal.ext.wizards.ForgeWizard;
import org.jboss.tools.forge.ui.internal.ext.wizards.ForgeWizardPage;
import org.jboss.tools.forge.ui.notifications.NotificationType;

/**
 */
public final class WizardDialogHelper {

	private final UIContextImpl context;
	private final Shell parentShell;

	public WizardDialogHelper(Shell parentShell, IStructuredSelection selection) {
		this.parentShell = parentShell;
		ForgeUIProvider provider = new ForgeUIProvider();
		this.context = new UIContextImpl(provider, selection);
	}

	public UIContextImpl getContext() {
		return context;
	}

	public List<UICommand> getAllCandidatesAsList() {
		List<UICommand> result = new ArrayList<>();
		CommandFactory commandFactory = FurnaceService.INSTANCE
				.lookup(CommandFactory.class);
		for (UICommand uiCommand : commandFactory.getCommands()) {
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

	public UICommand getCommand(String name) {
		CommandFactory commandFactory = FurnaceService.INSTANCE
				.lookup(CommandFactory.class);
		return commandFactory.getCommandByName(context, name);
	}

	public Map<String, UICommand> getAllCandidatesAsMap() {
		Map<String, UICommand> result = new TreeMap<>();
		CommandFactory commandFactory = FurnaceService.INSTANCE
				.lookup(CommandFactory.class);
		for (UICommand uiCommand : commandFactory.getCommands()) {
			try {
				if (!(uiCommand instanceof UIWizardStep)
						&& uiCommand.isEnabled(context)) {
					UICommandMetadata metadata = uiCommand
							.getMetadata(getContext());
					result.put(metadata.getName(), uiCommand);
				}
			} catch (Exception e) {
				ForgeUIPlugin.log(e);
			}

		}
		return result;
	}

	public void openWizard(String windowTitle, UICommand selectedCommand) {
		openWizard(windowTitle, selectedCommand, null);
	}

	public void openWizard(String windowTitle, UICommand selectedCommand,
			Map<String, ?> values) {
		CommandControllerFactory controllerFactory = FurnaceService.INSTANCE
				.lookup(CommandControllerFactory.class);
		ForgeUIRuntime runtime = new ForgeUIRuntime();
		CommandController controller = controllerFactory.createController(
				context, runtime, selectedCommand);
		try {
			controller.initialize();
		} catch (Exception e) {
			ForgeUIPlugin.displayMessage(windowTitle,
					"Error while initializing controller. Check logs",
					NotificationType.ERROR);
			ForgeUIPlugin.log(e);
			return;
		}
		ForgeWizard wizard = new ForgeWizard(windowTitle, controller, context);
		final WizardDialog wizardDialog;
		if (controller instanceof WizardCommandController) {
			WizardCommandController wizardController = (WizardCommandController) controller;
			if (values != null) {
				Map<String, InputComponent<?, ?>> inputs = wizardController
						.getInputs();
				for (String key : inputs.keySet()) {
					Object value = values.get(key);
					if (value != null)
						wizardController.setValueFor(key, value);
				}
				while (wizardController.canMoveToNextStep()) {
					try {
						wizardController.next().initialize();
					} catch (Exception e) {
						ForgeUIPlugin.log(e);
						break;
					}
					inputs = wizardController.getInputs();
					for (String key : inputs.keySet()) {
						Object value = values.get(key);
						if (value != null)
							wizardController.setValueFor(key, value);
					}
				}
				// Rewind
				while (wizardController.canMoveToPreviousStep()) {
					try {
						wizardController.previous();
					} catch (Exception e) {
						ForgeUIPlugin.log(e);
					}
				}
			}
			wizardDialog = new ForgeWizardDialog(parentShell, wizard,
					wizardController);
		} else {
			if (values != null) {
				for (Entry<String, ?> entry : values.entrySet()) {
					controller.setValueFor(entry.getKey(), entry.getValue());
				}
			}
			wizardDialog = new ForgeCommandDialog(parentShell, wizard);
		}
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

		if (controller.getInputs().isEmpty() && controller.canExecute()) {
			try {
				ApplicationWindow window = new ApplicationWindow(parentShell);
				wizard.performFinish(window, parentShell);
			} catch (InvocationTargetException | InterruptedException e) {
				ForgeUIPlugin.log(e);
			}
		} else {
			try {
				wizardDialog.open();
			} catch (Exception e) {
				ForgeUIPlugin.displayMessage("Error",
						"Error while opening wizard. See Error log",
						NotificationType.ERROR);
				ForgeUIPlugin.log(e);
			}
		}
	}
}
