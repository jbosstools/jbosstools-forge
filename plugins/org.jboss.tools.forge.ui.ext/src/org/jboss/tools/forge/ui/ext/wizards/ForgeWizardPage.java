/*
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.tools.forge.ui.ext.wizards;

import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
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
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.tools.forge.ui.ext.ForgeUIPlugin;
import org.jboss.tools.forge.ui.ext.context.UIBuilderImpl;
import org.jboss.tools.forge.ui.ext.context.UIContextImpl;
import org.jboss.tools.forge.ui.ext.context.UIValidationContextImpl;
import org.jboss.tools.forge.ui.ext.control.ControlBuilder;
import org.jboss.tools.forge.ui.ext.control.ControlBuilderRegistry;

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

	private ComponentControlEntry[] componentControlEntries;

	public ForgeWizardPage(Wizard wizard, UICommand command,
			UIContextImpl contextImpl) {
		super(command.getMetadata().getName());
		setWizard(wizard);
		setPageComplete(false);
		UICommandMetadata id = command.getMetadata();
		setTitle(id.getName());
		setDescription(id.getDescription());
		setImageDescriptor(ForgeUIPlugin.getForgeLogo());
		this.uiCommand = command;
		this.uiContext = contextImpl;
	}

	public UICommand getUICommand() {
		return uiCommand;
	}

	@Override
	public void createControl(Composite parent) {
		uiBuilder = new UIBuilderImpl(uiContext);
		try {
			uiCommand.initializeUI(uiBuilder);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		List<InputComponent<?, ?>> inputs = uiBuilder.getInputs();
		createControls(parent, inputs);
	}

	@SuppressWarnings("unchecked")
	protected void createControls(Composite parent,
			List<InputComponent<?, ?>> inputs) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 2;
		layout.verticalSpacing = 9;

		// Init component control array
		int size = inputs.size();
		componentControlEntries = new ComponentControlEntry[size];

		for (int i = 0; i < size; i++) {
			final InputComponent<?, ?> input = inputs.get(i);
			ControlBuilder controlBuilder = ControlBuilderRegistry.INSTANCE
					.getBuilderFor(input);
			Control control = controlBuilder.build(this,
					(InputComponent<?, Object>) input, container);

			// Update page status
			if (control.isListening(SWT.Modify)) {
				control.addListener(SWT.Modify, this);
			} else {
				control.addListener(SWT.DefaultSelection, this);
				control.addListener(SWT.Selection, this);
			}

			// if a page is changed, subsequent pages should be invalidated
			ChangeListener cl = new ChangeListener();
			control.addListener(SWT.Modify, cl);
			control.addListener(SWT.Selection, cl);
			componentControlEntries[i] = new ComponentControlEntry(input,
					control);
		}
		setPageComplete(validatePage());

		// Clear error messages when opening
		setErrorMessage(null);
		setMessage(null);
		setControl(container);
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
				InputComponent<?, ?> key = entry.getComponent();
				Control value = entry.getControl();
				value.setEnabled(key.isEnabled());
			}
		}

		// Validate required
		if (uiBuilder != null) {
			for (InputComponent<?, ?> input : uiBuilder.getInputs()) {
				String requiredMsg = InputComponents.validateRequired(input);
				if (requiredMsg != null) {
					setErrorMessage(requiredMsg);
					return false;
				}
			}
		}

		// Invoke custom validation
		UIValidationContextImpl validationContext = new UIValidationContextImpl(
				uiContext);
		// invokes the validation in the current UICommand
		uiCommand.validate(validationContext);
		List<String> errors = validationContext.getErrors();
		boolean noErrors = errors.isEmpty();
		if (!noErrors) {
			setErrorMessage(errors.get(0));
		}
		// if no errors were found, the page is ready to go to the next step
		return noErrors;
	}

	@Override
	public void performHelp() {
		UICommandMetadata metadata = uiCommand.getMetadata();
		URL docLocation = metadata.getDocLocation();
		if (docLocation != null) {
			// PlatformUI.getWorkbench().getHelpSystem().displayHelp(docLocation.toExternalForm());
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

	@Override
	public IWizardPage getNextPage() {
		IWizardPage nextPage = super.getNextPage();
		this.changed = false;
		return nextPage;
	}

	@Override
	public IWizardPage getPreviousPage() {
		IWizardPage previousPage = super.getPreviousPage();
		this.changed = false;
		return previousPage;
	}

	/**
	 * Stores a component/control relationship
	 */
	private class ComponentControlEntry {
		private InputComponent<?, ?> component;
		private Control control;

		public ComponentControlEntry(InputComponent<?, ?> component,
				Control control) {
			this.component = component;
			this.control = control;
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
				ForgeWizardPage.this.changed = true;
			}
		}
	}

	public boolean isChanged() {
		return changed;
	}
}