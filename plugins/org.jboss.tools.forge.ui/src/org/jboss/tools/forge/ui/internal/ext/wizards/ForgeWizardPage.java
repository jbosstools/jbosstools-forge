/**
 * Copyright (c) Red Hat, Inc., contributors and others 2004 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.forge.ui.internal.ext.wizards;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.controller.WizardCommandController;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.output.UIMessage;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.tools.forge.ui.internal.ForgeUIPlugin;
import org.jboss.tools.forge.ui.internal.ext.control.ControlBuilder;
import org.jboss.tools.forge.ui.internal.ext.control.ControlBuilderRegistry;
import org.jboss.tools.forge.ui.notifications.NotificationType;

/**
 * A Forge Wizard Page
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
public class ForgeWizardPage extends WizardPage implements Listener {
	private CommandController controller;
	private boolean changed;

	private List<ComponentControlEntry> componentControlEntries = new ArrayList<>();

	public ForgeWizardPage(ForgeWizard wizard, CommandController controller) {
		super(controller.getMetadata().getName());
		setWizard(wizard);
		setPageComplete(false);
		this.controller = controller;
		UICommandMetadata id = controller.getMetadata();
		setTitle(id.getName());
		setDescription(id.getDescription());
		setImageDescriptor(ForgeUIPlugin.getForgeLogo());
	}

	public CommandController getController() {
		return controller;
	}

	@Override
	public ForgeWizard getWizard() {
		return (ForgeWizard) super.getWizard();
	}

	@Override
	public void createControl(Composite parent) {
		try {
			controller.initialize();
		} catch (Exception e) {
			ForgeUIPlugin.log(e);
			ForgeUIPlugin.displayMessage("Error has occurred!",
					"See Error Log for details", NotificationType.ERROR);
			return;
		}
		Map<String, InputComponent<?, ?>> inputs = controller.getInputs();
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 3;
		layout.verticalSpacing = 9;

		// Init component control array
		for (Entry<String, InputComponent<?, ?>> entry : inputs.entrySet()) {
			String inputName = entry.getKey();
			InputComponent<?, ?> input = entry.getValue();
			ControlBuilder<Control> controlBuilder = ControlBuilderRegistry
					.getBuilderFor(input);
			Control control = controlBuilder.build(this, input, inputName,
					container);

			if (input.isRequired()) {
				decorateRequiredField(input, control);
			}
			Control[] modifiableControls = controlBuilder
					.getModifiableControlsFor(control);
			for (Control child : modifiableControls) {
				registerListeners(child);
			}
			componentControlEntries.add(new ComponentControlEntry(input,
					controlBuilder, control));
		}
		initValidatePage();
		setControl(container);
	}

	private void clearMessages() {
		setErrorMessage(null);
		setMessage(null);
	}

	private void registerListeners(Control control) {
		// Update page status
		control.addListener(SWT.Modify, this);
		control.addListener(SWT.DefaultSelection, this);
		control.addListener(SWT.Selection, this);

		// if a page is changed, subsequent pages should be invalidated
		ChangeListener cl = new ChangeListener();
		control.addListener(SWT.Modify, cl);
		control.addListener(SWT.Selection, cl);
	}

	/**
	 * Decorate required field
	 */
	private void decorateRequiredField(final InputComponent<?, ?> input,
			Control control) {
		FieldDecoration completerIndicator = FieldDecorationRegistry
				.getDefault().getFieldDecoration(
						FieldDecorationRegistry.DEC_REQUIRED);
		ControlDecoration dec = new ControlDecoration(control, SWT.LEFT
				| SWT.CENTER);
		dec.setImage(completerIndicator.getImage());
		dec.setDescriptionText(completerIndicator.getDescription());
	}

	/**
	 * The <code>ForgeWizardPage</code> implementation of this
	 * <code>Listener</code> method handles all events and enablements for
	 * controls on this page.
	 */
	@Override
	public void handleEvent(final Event event) {
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				if (isCurrentPage()) {
					setPageComplete(validatePage());
					// Refresh the buttons
					getContainer().updateButtons();
				}
				event.doit = true;
			}
		});
	}

	private void updatePageState() {
		// Change enabled state
		if (componentControlEntries != null) {
			for (ComponentControlEntry entry : componentControlEntries) {
				InputComponent<?, ?> component = entry.getComponent();
				ControlBuilder<Control> controlBuilder = entry
						.getControlBuilder();
				Control control = entry.getControl();
				controlBuilder.updateState(control, component);
			}
		}
	}

	/**
	 * Called when the page is opened for the first time.
	 *
	 * It should display error messages unless the error message indicates a
	 * required field
	 */
	private void initValidatePage() {
		updatePageState();
		for (ComponentControlEntry entry : componentControlEntries) {
			if (InputComponents.validateRequired(entry.getComponent()) != null) {
				setPageComplete(false);
				return;
			}
		}
		setPageComplete(validatePage());
	}

	/**
	 * Returns whether this page's controls currently all contain valid values.
	 * It also calls {@link ForgeWizardPage#setErrorMessage(String)} if an error
	 * is found
	 *
	 * @return <code>true</code> if all controls are valid, and
	 *         <code>false</code> if at least one is invalid
	 */
	private boolean validatePage() {
		clearMessages();

		updatePageState();
		for (UIMessage message : controller.validate()) {
			switch (message.getSeverity()) {
			case ERROR:
				setErrorMessage(message.getDescription());
				return false;
			case WARN:
				setWarningMessage(message.getDescription());
				return true;
			case INFO:
				setInfoMessage(message.getDescription());
				return true;
			}
		}
		return true;
	}

	public void setInfoMessage(String warningMessage) {
		setMessage(warningMessage, DialogPage.INFORMATION);
		if (isCurrentPage()) {
			getContainer().updateMessage();
		}
	}

	public void setWarningMessage(String warningMessage) {
		setMessage(warningMessage, DialogPage.WARNING);
		if (isCurrentPage()) {
			getContainer().updateMessage();
		}
	}

	@Override
	public void dispose() {
		super.dispose();
		componentControlEntries.clear();
	}

	@Override
	public boolean canFlipToNextPage() {
		boolean result;
		if (isWizard()) {
			result = ((WizardCommandController) controller).canMoveToNextStep();
		} else {
			// Single page wizards cannot move to next step
			result = false;
		}
		return isPageComplete() && result;
	}

	private boolean isWizard() {
		return controller instanceof WizardCommandController;
	}

	public void setChanged(boolean changed) {
		this.changed = changed;
	}

	public boolean isChanged() {
		return changed;
	}

	/**
	 * Stores a component/control relationship
	 */
	class ComponentControlEntry {
		private InputComponent<?, ?> component;
		private ControlBuilder<Control> controlBuilder;
		private Control control;

		public ComponentControlEntry(InputComponent<?, ?> component,
				ControlBuilder<Control> controlBuilder, Control control) {
			this.component = component;
			this.controlBuilder = controlBuilder;
			this.control = control;
		}

		public ControlBuilder<Control> getControlBuilder() {
			return controlBuilder;
		}

		public InputComponent<?, ?> getComponent() {
			return component;
		}

		public Control getControl() {
			return control;
		}
	}

	private class ChangeListener implements Listener {
		@Override
		public void handleEvent(Event evt) {
			if (isCurrentPage()) {
				setChanged(true);
			}
		}
	}
}