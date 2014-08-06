/**
 * Copyright (c) Red Hat, Inc., contributors and others 2013 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.forge.ui.internal.ext.control;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.tools.forge.core.furnace.FurnaceService;
import org.jboss.tools.forge.ui.internal.ext.wizards.ForgeWizardPage;

@SuppressWarnings("unchecked")
public class CheckboxControlBuilder extends ControlBuilder<Button> {

	@Override
	public Button build(final ForgeWizardPage page,
			final InputComponent<?, ?> input, final String inputName,
			final Composite container) {

		// Checkbox should be placed in second column
		Label dummy1 = new Label(container, SWT.NONE);
		dummy1.setText("");

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
			Converter<Object, Boolean> converter = (Converter<Object, Boolean>) converterFactory
					.getConverter(input.getValueType(), Boolean.class);
			Boolean value = converter.convert(InputComponents
					.getValueFor(input));
			cmb.setSelection(value == null ? false : value);
		}

		cmb.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean selection = ((Button) e.widget).getSelection();
				CommandController controller = page.getController();
				controller.setValueFor(inputName, selection);
			}
		});

		// skip third column
		Label dummy2 = new Label(container, SWT.NONE);
		dummy2.setText("");

		return cmb;
	}

	@Override
	protected Class<Boolean> getProducedType() {
		return Boolean.class;
	}

	@Override
	protected String getSupportedInputType() {
		return InputType.CHECKBOX;
	}

	@Override
	protected Class<?>[] getSupportedInputComponentTypes() {
		return new Class<?>[] { UIInput.class };
	}
}
