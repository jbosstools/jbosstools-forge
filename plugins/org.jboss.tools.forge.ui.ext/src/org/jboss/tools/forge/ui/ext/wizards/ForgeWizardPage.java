/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.forge.ui.ext.wizards;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.tools.forge.ui.ext.ForgeUIPlugin;
import org.jboss.tools.forge.ui.ext.context.UIBuilderImpl;
import org.jboss.tools.forge.ui.ext.context.UIContextImpl;
import org.jboss.tools.forge.ui.ext.context.UIValidationContextImpl;
import org.jboss.tools.forge.ui.ext.control.ControlBuilder;
import org.jboss.tools.forge.ui.ext.control.ControlBuilderRegistry;
import org.jboss.tools.forge.ui.notifications.NotificationType;

/**
 * A Forge Wizard Page
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */
public class ForgeWizardPage extends WizardPage implements Listener {
	private UICommand uiCommand;
	private UIContextImpl uiContext;
	private UIBuilderImpl uiBuilder;
	private boolean changed;
	private final boolean subflowHead;

	private ComponentControlEntry[] componentControlEntries;

	public ForgeWizardPage(ForgeWizard wizard, UICommand command,
			UIContextImpl contextImpl, boolean startsSubflow) {
		super(command.getMetadata(contextImpl).getName());
		setWizard(wizard);
		setPageComplete(false);
		UICommandMetadata id = command.getMetadata(contextImpl);
		setTitle(id.getName());
		setDescription(id.getDescription());
		setImageDescriptor(ForgeUIPlugin.getForgeLogo());
		this.uiCommand = command;
		this.uiContext = contextImpl;
		this.subflowHead = startsSubflow;
	}

	public UICommand getUICommand() {
		return uiCommand;
	}

	public UIContextImpl getUIContext() {
		return uiContext;
	}

	@Override
	public void createControl(Composite parent) {
		uiBuilder = new UIBuilderImpl(uiContext);
		try {
			uiCommand.initializeUI(uiBuilder);
		} catch (Exception e) {
			ForgeUIPlugin.log(e);
			ForgeUIPlugin.displayMessage("Error has occurred!",
					"See Error Log for details", NotificationType.ERROR);
			return;
		}

		List<InputComponent<?, Object>> inputs = uiBuilder.getInputs();
		createControls(parent, inputs);
	}

	protected void createControls(Composite parent,
			List<InputComponent<?, Object>> inputs) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 2;
		layout.verticalSpacing = 9;

		// Init component control array
		int size = inputs.size();
		componentControlEntries = new ComponentControlEntry[size];

		for (int i = 0; i < size; i++) {
			final InputComponent<?, Object> input = inputs.get(i);
			ControlBuilder<Control> controlBuilder = ControlBuilderRegistry
					.getBuilderFor(input);
			Control control = controlBuilder.build(this,
					(InputComponent<?, Object>) input, container);

			if (input.isRequired()) {
				decorateRequiredField(input, control);
			}
			Control[] modifiableControls = controlBuilder
					.getModifiableControlsFor(control);
			for (Control child : modifiableControls) {
				registerListeners(child);
			}
			componentControlEntries[i] = new ComponentControlEntry(input,
					controlBuilder, control);
		}
		setPageComplete(validatePage());

		// Clear error messages when opening
		clearMessages();
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
	public void handleEvent(Event event) {
		if (isCurrentPage()) {
			setPageComplete(validatePage());
			// Refresh the buttons
			getContainer().updateButtons();
		}
		event.doit = true;
	}

	/**
	 * Returns whether this page's controls currently all contain valid values.
	 * It also calls {@link ForgeWizardPage#setErrorMessage(String)} if an error
	 * is found
	 * 
	 * @return <code>true</code> if all controls are valid, and
	 *         <code>false</code> if at least one is invalid
	 */
	public boolean validatePage() {
		// clear error message
		setErrorMessage(null);

		// Change enabled state
		if (componentControlEntries != null) {
			for (ComponentControlEntry entry : componentControlEntries) {
				InputComponent<?, Object> component = entry.getComponent();
				ControlBuilder<Control> controlBuilder = entry
						.getControlBuilder();
				Control control = entry.getControl();
				controlBuilder.updateState(control, component);
			}
		}

		// Invoke custom validation
		UIValidationContextImpl validationContext = new UIValidationContextImpl(
				uiContext);
		List<String> errors = validationContext.getErrors();
		// Validate required
		if (uiBuilder != null) {
			for (InputComponent<?, ?> input : uiBuilder.getInputs()) {
				validationContext.setCurrentInputComponent(input);
				input.validate(validationContext);
				if (!errors.isEmpty()) {
					setErrorMessage(errors.get(0));
					return false;
				}
			}
		}
		validationContext.setCurrentInputComponent(null);
		// invokes the validation in the current UICommand
		uiCommand.validate(validationContext);
		boolean noErrors = errors.isEmpty();
		if (noErrors) {
			List<String> warnings = validationContext.getWarnings();
			if (!warnings.isEmpty()) {
				setWarningMessage(warnings.get(0));
			} else {
				List<String> infos = validationContext.getInformations();
				if (!infos.isEmpty()) {
					setInfoMessage(infos.get(0));
				} else {
					clearMessages();
				}
			}
		} else {
			setErrorMessage(errors.get(0));
		}
		// if no errors were found, the page is ready to go to the next step
		return noErrors;
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
		if (componentControlEntries != null) {
			Arrays.fill(componentControlEntries, null);
			componentControlEntries = null;
		}
	}

	/**
	 * Stores a component/control relationship
	 */
	private class ComponentControlEntry {
		private InputComponent<?, Object> component;
		private ControlBuilder<Control> controlBuilder;
		private Control control;

		public ComponentControlEntry(InputComponent<?, Object> component,
				ControlBuilder<Control> controlBuilder, Control control) {
			this.component = component;
			this.controlBuilder = controlBuilder;
			this.control = control;
		}

		public ControlBuilder<Control> getControlBuilder() {
			return controlBuilder;
		}

		public InputComponent<?, Object> getComponent() {
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

	public void setChanged(boolean changed) {
		this.changed = changed;
	}

	public boolean isChanged() {
		return changed;
	}

	/**
	 * @return the subflowHead
	 */
	public boolean isSubflowHead() {
		return subflowHead;
	}
}