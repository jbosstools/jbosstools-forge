/**
 * Copyright (c) Red Hat, Inc., contributors and others 2013 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.forge.ui.internal.ext.control;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.tools.forge.core.furnace.FurnaceService;
import org.jboss.tools.forge.ui.internal.ForgeUIPlugin;
import org.jboss.tools.forge.ui.internal.ext.wizards.ForgeWizardPage;

@SuppressWarnings("unchecked")
public class TextAreaBoxControlBuilder extends ControlBuilder<Text> {

	@Override
	public Text build(final ForgeWizardPage page,
			final InputComponent<?, ?> input, final String inputName,
			final Composite container) {
		// Create the label
		Label label = new Label(container, SWT.NULL);
		label.setText(getMnemonicLabel(input, true));

		final Text txt = new Text(container, SWT.MULTI | SWT.BORDER | SWT.WRAP
				| SWT.V_SCROLL);
		GridData textareaLayoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
		textareaLayoutData.heightHint = 100;
		txt.setLayoutData(textareaLayoutData);
		txt.setToolTipText(input.getDescription());
		// Set Default Value
		final ConverterFactory converterFactory = FurnaceService.INSTANCE
				.getConverterFactory();
		if (converterFactory != null) {
			Converter<Object, String> converter = (Converter<Object, String>) converterFactory
					.getConverter(input.getValueType(), String.class);
			String value = converter
					.convert(InputComponents.getValueFor(input));
			txt.setText(value == null ? "" : value);
		}

		txt.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				CommandController controller = page.getController();
				try {
					controller.setValueFor(inputName, txt.getText());
				} catch (Exception ex) {
					ForgeUIPlugin.log(ex);
					controller.setValueFor(inputName, null);
				}
			}
		});
		setupAutoCompleteForText(page.getWizard().getUIContext(), input,
				InputComponents.getCompleterFor(input), txt);

		// skip the third column
		Label dummy = new Label(container, SWT.NONE);
		dummy.setText("");

		return txt;
	}

	@Override
	protected Class<String> getProducedType() {
		return String.class;
	}

	@Override
	protected String getSupportedInputType() {
		return InputType.TEXTAREA;
	}

	@Override
	protected Class<?>[] getSupportedInputComponentTypes() {
		return new Class<?>[] { UIInput.class };
	}
}
