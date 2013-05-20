/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.tools.forge.ui.ext.control;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.hints.InputTypes;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.tools.forge.ext.core.ForgeService;
import org.jboss.tools.forge.ui.ext.wizards.ForgeWizardPage;

public class PasswordTextBoxControlBuilder extends ControlBuilder {

	@Override
	public Control build(ForgeWizardPage page,
			final InputComponent<?, Object> input, final Composite container) {
		// Create the label
		Label label = new Label(container, SWT.NULL);
		label.setText(input.getLabel() == null ? input.getName() : input
				.getLabel());

		final Text txt = new Text(container, SWT.BORDER | SWT.SINGLE);
		txt.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		txt.setEchoChar('*');

		// Set Default Value
		final ConverterFactory converterFactory = ForgeService.INSTANCE
				.getConverterFactory();
		if (converterFactory != null) {
			Converter<Object, String> converter = converterFactory
					.getConverter(input.getValueType(), String.class);
			String value = converter
					.convert(InputComponents.getValueFor(input));
			txt.setText(value == null ? "" : value);
		}

		txt.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				InputComponents.setValueFor(converterFactory, input,
						txt.getText());
			}
		});
		return txt;
	}

	@Override
	protected Class<String> getProducedType() {
		return String.class;
	}

	@Override
	protected InputType getSupportedInputType() {
		return InputTypes.SECRET;
	}

	@Override
	protected Class<?>[] getSupportedInputComponentTypes() {
		return new Class<?>[] { UIInput.class };
	}
}
