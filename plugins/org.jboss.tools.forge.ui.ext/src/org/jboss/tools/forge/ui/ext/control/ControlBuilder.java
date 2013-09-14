/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.tools.forge.ui.ext.control;

import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.rse.ui.Mnemonics;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.UICompleter;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.forge.furnace.proxy.Proxies;
import org.jboss.tools.forge.ext.core.FurnaceService;
import org.jboss.tools.forge.ui.ext.autocomplete.InputComponentProposalProvider;
import org.jboss.tools.forge.ui.ext.context.UIContextImpl;
import org.jboss.tools.forge.ui.ext.wizards.ForgeWizardPage;

/**
 * Builds a control
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */
public abstract class ControlBuilder {

	/**
	 * Builds an Eclipse {@link Control} object based on the input
	 * 
	 * @param page
	 *            TODO
	 * @param input
	 * @param container
	 *            the container this control will be placed on
	 * 
	 * @return
	 */
	public abstract Control build(final ForgeWizardPage page,
			final InputComponent<?, Object> input, final Composite container);

	/**
	 * Returns the supported type this control may produce
	 * 
	 * @return
	 */
	protected abstract Class<?> getProducedType();

	/**
	 * Returns the supported input type for this component
	 * 
	 * @return
	 */
	protected abstract InputType getSupportedInputType();

	/**
	 * Returns the subclasses of {@link InputComponent}
	 * 
	 * @return
	 */
	protected abstract Class<?>[] getSupportedInputComponentTypes();

	/**
	 * Tests if this builder may handle this specific input
	 * 
	 * @param input
	 * @return
	 */
	public boolean handles(InputComponent<?, ?> input) {
		boolean handles = false;
		for (Class<?> inputType : getSupportedInputComponentTypes()) {
			if (inputType.isInstance(input)) {
				handles = true;
				break;
			}
		}

		if (handles) {
			InputType inputTypeHint = InputComponents.getInputType(input);
			if (inputTypeHint != null && inputTypeHint != InputType.DEFAULT) {
				handles = Proxies.areEquivalent(inputTypeHint,
						getSupportedInputType());
			} else {
				// Fallback to standard type
				handles = getProducedType().isAssignableFrom(
						input.getValueType());
			}
		}

		return handles;
	}

	public void setEnabled(Control control, boolean enabled) {
		if (control instanceof Composite) {
			Composite c = (Composite) control;
			for (Control child : c.getChildren()) {
				child.setEnabled(enabled);
			}
		} else {
			control.setEnabled(enabled);
		}
	}

	/**
	 * Return the controls that accept listeners for modifications
	 */
	public Control[] getModifiableControlsFor(Control control) {
		if (control instanceof Composite) {
			return ((Composite) control).getChildren();
		} else {
			return new Control[] { control };
		}
	}

	protected ContentProposalAdapter setupAutoCompleteForText(
			UIContextImpl context, InputComponent<?, Object> input,
			UICompleter<Object> completer, Text text) {
		ContentProposalAdapter result = null;
		if (completer != null) {
			ControlDecoration dec = new ControlDecoration(text, SWT.TOP
					| SWT.LEFT);
			// Add lightbulb
			FieldDecoration completerIndicator = FieldDecorationRegistry
					.getDefault().getFieldDecoration(
							FieldDecorationRegistry.DEC_CONTENT_PROPOSAL);
			dec.setImage(completerIndicator.getImage());
			dec.setDescriptionText(completerIndicator.getDescription());

			// Register auto-complete
			KeyStroke activationKeyStroke = KeyStroke.getInstance(SWT.CONTROL,
					SWT.SPACE);
			result = new ContentProposalAdapter(text, new TextContentAdapter(),
					new InputComponentProposalProvider(context, input,
							completer), activationKeyStroke, null);
			result.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);
		}
		return result;
	}

	protected String getMnemonicLabel(InputComponent<?, Object> input,
			boolean addColon) {
		String label = InputComponents.getLabelFor(input, addColon);
		char shortName = input.getShortName();
		if (shortName != InputComponents.DEFAULT_SHORT_NAME) {
			label = Mnemonics.applyMnemonic(label, shortName);
		}
		return label;
	}

	protected ConverterFactory getConverterFactory() {
		return FurnaceService.INSTANCE.getConverterFactory();
	}
}