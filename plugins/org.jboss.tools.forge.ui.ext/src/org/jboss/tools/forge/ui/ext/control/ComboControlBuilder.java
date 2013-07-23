/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.tools.forge.ui.ext.control;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.forge.furnace.proxy.Proxies;
import org.jboss.tools.forge.ext.core.FurnaceService;
import org.jboss.tools.forge.ui.ext.wizards.ForgeWizardPage;

public class ComboControlBuilder extends ControlBuilder {

	@Override
	@SuppressWarnings({ "unchecked" })
	public Combo build(ForgeWizardPage page,
			final InputComponent<?, Object> input, final Composite container) {
		// Create the label
		Label label = new Label(container, SWT.NULL);
		label.setText(InputComponents.getLabelFor(input, true));

		final Combo combo = new Combo(container, SWT.BORDER | SWT.DROP_DOWN
				| SWT.READ_ONLY);

		final ConverterFactory converterFactory = FurnaceService.INSTANCE
				.getConverterFactory();
		UISelectOne<Object> selectOne = (UISelectOne<Object>) input;
		Converter<Object, String> converter = (Converter<Object, String>) InputComponents
				.getItemLabelConverter(converterFactory, selectOne);
		String value = converter.convert(InputComponents.getValueFor(input));
		final Map<String, Object> items = new LinkedHashMap<String, Object>();
		Iterable<Object> valueChoices = selectOne.getValueChoices();
		if (valueChoices != null) {
			for (Object choice : valueChoices) {
				String itemLabel = converter.convert(choice);
				items.put(itemLabel, Proxies.unwrap(choice));
				combo.add(itemLabel);
			}
		}
		combo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int selectionIndex = combo.getSelectionIndex();
				if (selectionIndex != -1) {
					String item = combo.getItem(selectionIndex);
					Object selectedObj = items.get(item);
					InputComponents.setValueFor(converterFactory, input,
							selectedObj);
				}
			}
		});

		// Set Default Value
		if (value == null) {
			if (combo.getVisibleItemCount() > 0) {
				combo.select(0);
			}
		} else {
			combo.setText(value);
		}

		// Cleaning the map when the input is disposed
		combo.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				items.clear();
			}
		});
		combo.setToolTipText(input.getDescription());
		return combo;
	}

	@Override
	protected Class<Object> getProducedType() {
		return Object.class;
	}

	@Override
	protected InputType getSupportedInputType() {
		return InputType.SELECT_ONE_DROPDOWN;
	}

	@Override
	protected Class<?>[] getSupportedInputComponentTypes() {
		return new Class<?>[] { UISelectOne.class };
	}
}
