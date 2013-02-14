/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.tools.forge.ui.control;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.jboss.forge.convert.Converter;
import org.jboss.forge.convert.ConverterFactory;
import org.jboss.forge.ui.UIInput;
import org.jboss.forge.ui.UIInputComponent;
import org.jboss.forge.ui.UISelectOne;
import org.jboss.forge.ui.hints.InputType;
import org.jboss.forge.ui.hints.InputTypes;
import org.jboss.tools.forge.ui.wizards.ForgeWizardPage;

@SuppressWarnings("rawtypes")
public class EnumComboControlBuilder extends ControlBuilder {

	@Override
	@SuppressWarnings({ "unchecked" })
	public Control build(ForgeWizardPage page,
			final UIInputComponent<?, Object> input, final Composite container) {
		final Combo combo = new Combo(container, SWT.BORDER | SWT.SINGLE
				| SWT.READ_ONLY);
		Enum[] enumConstants = input.getValueType().asSubclass(Enum.class)
				.getEnumConstants();
		for (Enum enum1 : enumConstants) {
			combo.add(enum1.name());
		}
		// Set Default Value
		ConverterFactory converterFactory = getConverterFactory();
		if (converterFactory != null) {
			Converter<Object, String> converter = converterFactory
					.getConverter(input.getValueType(), String.class);
			String value = converter.convert(getValueFor(input));
			combo.setText(value == null ? "" : value);
		}

		combo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int selectionIndex = combo.getSelectionIndex();
				if (selectionIndex != -1) {
					String item = combo.getItem(selectionIndex);
					Class valueType = input.getValueType();
					setValueFor(input, Enum.valueOf(valueType, item));
				}
			}
		});
		return combo;
	}

	@Override
	protected Class<Enum> getProducedType() {
		return Enum.class;
	}

	@Override
	protected InputType getSupportedInputType() {
		return InputTypes.SELECT_ONE_DROPDOWN;
	}

	@Override
	protected Iterable<Class<?>> getSupportedInputComponentTypes() {
		List<Class<?>> result = new ArrayList<Class<?>>();
		result.add(UISelectOne.class);
		result.add(UIInput.class);
		return result;
	}
}
