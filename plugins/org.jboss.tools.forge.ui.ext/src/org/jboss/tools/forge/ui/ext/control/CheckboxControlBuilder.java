/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.tools.forge.ui.ext.control;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.tools.forge.ext.core.FurnaceService;
import org.jboss.tools.forge.ui.ext.wizards.ForgeWizardPage;

public class CheckboxControlBuilder extends ControlBuilder<Button> {

	@Override
	public Button build(ForgeWizardPage page,
			final InputComponent<?, Object> input, final Composite container) {
		// Checkbox should be placed in second column
		new Label(container, SWT.NONE);
		GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
		layoutData.horizontalSpan = 1;
		Button cmb = new Button(container, SWT.CHECK);
		cmb.setLayoutData(layoutData);
		cmb.setText(getMnemonicLabel(input, false));
		cmb.setToolTipText(input.getDescription());
		// Set Default Value
		final ConverterFactory converterFactory = FurnaceService.INSTANCE
				.getConverterFactory();
		if (converterFactory != null) {
			Converter<Object, Boolean> converter = converterFactory
					.getConverter(input.getValueType(), Boolean.class);
			Boolean value = converter.convert(InputComponents
					.getValueFor(input));
			cmb.setSelection(value == null ? false : value);
		}

		// cmd.setSelection(value == null ? false : value);
		cmb.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean selection = ((Button) e.widget).getSelection();
				InputComponents.setValueFor(converterFactory, input, selection);
			}
		});
		return cmb;
	}

	@Override
	protected Class<Boolean> getProducedType() {
		return Boolean.class;
	}

	@Override
	protected InputType getSupportedInputType() {
		return InputType.CHECKBOX;
	}

	@Override
	protected Class<?>[] getSupportedInputComponentTypes() {
		return new Class<?>[] { UIInput.class };
	}
}
