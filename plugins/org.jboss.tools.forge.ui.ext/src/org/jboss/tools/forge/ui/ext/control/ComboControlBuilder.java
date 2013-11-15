/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.tools.forge.ui.ext.control;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.tools.forge.ext.core.FurnaceService;
import org.jboss.tools.forge.ui.ext.wizards.ForgeWizardPage;

public class ComboControlBuilder extends ControlBuilder<Combo> {

	private static final Set<Combo> COMBO_STATUS_CHANGE = new HashSet<Combo>();

	@Override
	public Combo build(ForgeWizardPage page,
			final InputComponent<?, Object> input, final Composite container) {

		// Create the label
		Label label = new Label(container, SWT.NULL);
		label.setText(getMnemonicLabel(input, true));

		final Combo combo = new Combo(container, SWT.BORDER | SWT.DROP_DOWN
				| SWT.READ_ONLY);
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		combo.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				String text = combo.getText();
				InputComponents.setValueFor(
						FurnaceService.INSTANCE.getConverterFactory(), input,
						text);
			}
		});
		combo.setToolTipText(input.getDescription());

		// skip the third column
		Label dummy = new Label(container, SWT.NONE);
		dummy.setText("");

		updateValues(combo, input);
		return combo;
	}

	@Override
	public void setEnabled(Combo control, boolean enabled) {
		if (enabled != control.isEnabled()) {
			control.setEnabled(enabled);
		}
	}

	@Override
	protected Class<Object> getProducedType() {
		return Object.class;
	}

	@Override
	protected InputType getSupportedInputType() {
		return InputType.DROPDOWN;
	}

	@Override
	protected Class<?>[] getSupportedInputComponentTypes() {
		return new Class<?>[] { UISelectOne.class };
	}

	@Override
	public Control[] getModifiableControlsFor(Combo control) {
		return new Control[] { control };
	}

	@Override
	public void updateState(Combo combo, InputComponent<?, Object> input) {
		if (!COMBO_STATUS_CHANGE.add(combo)) {
			return;
		}
		try {
			super.updateState(combo, input);
			updateValues(combo, input);
		} finally {
			COMBO_STATUS_CHANGE.remove(combo);
		}
	}

	private Converter<Object, String> getConverter(UISelectOne<Object> selectOne) {
		return (Converter<Object, String>) InputComponents
				.getItemLabelConverter(
						FurnaceService.INSTANCE.getConverterFactory(),
						selectOne);
	}

	@SuppressWarnings("unchecked")
	private void updateValues(Combo combo, InputComponent<?, Object> input) {
		UISelectOne<Object> selectOne = (UISelectOne<Object>) input;
		updateValueChoices(combo, selectOne);
		updateDefaultValue(combo, selectOne);
	}

	private void updateDefaultValue(Combo combo, UISelectOne<Object> selectOne) {
		Converter<Object, String> converter = getConverter(selectOne);
		String value = converter
				.convert(InputComponents.getValueFor(selectOne));
		if (value == null) {
			combo.setText("");
		} else if (!value.equals(combo.getText())) {
			combo.setText(value);
		}
	}

	private void updateValueChoices(Combo combo, UISelectOne<Object> selectOne) {
		List<String> newItems = new ArrayList<String>();
		Iterable<Object> valueChoices = selectOne.getValueChoices();
		Converter<Object, String> converter = getConverter(selectOne);
		if (valueChoices != null) {
			for (Object choice : valueChoices) {
				String itemLabel = converter.convert(choice);
				newItems.add(itemLabel);
			}
		}

		int newSize = newItems.size();
		String[] newItemsArray = newItems.toArray(new String[newSize]);
		String[] oldItems = combo.getItems();
		if (Arrays.equals(newItemsArray, oldItems) == false) {
			selectOne.setValue(null);
			combo.setItems(newItemsArray);
		}
	}

}
