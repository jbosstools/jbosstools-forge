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
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
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

public class ComboControlBuilder extends ControlBuilder<Combo> {
	
	
	private static final ConverterFactory CONVERTER_FACTORY = 
			FurnaceService.INSTANCE.getConverterFactory();
	
	@Override
	public Combo build(ForgeWizardPage page,
			final InputComponent<?, Object> input, final Composite container) {
		// Create the label
		Label label = new Label(container, SWT.NULL);
		label.setText(getMnemonicLabel(input, true));

		final Combo combo = new Combo(container, SWT.BORDER | SWT.DROP_DOWN
				| SWT.READ_ONLY);

		combo.addModifyListener(new ModifyListener() {			
			@Override
			public void modifyText(ModifyEvent e) {
				Object selectedObj = getItems(combo).get(combo.getText());
				InputComponents.setValueFor(CONVERTER_FACTORY, input,
						selectedObj);
			}
		});

		combo.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				getItems(combo).clear();
			}
		});
		combo.setToolTipText(input.getDescription());
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
		super.updateState(combo, input);
		updateValues(combo, input);
	}
	
	private Converter<Object, String> getConverter(UISelectOne<Object> selectOne) {
		return (Converter<Object, String>)InputComponents.getItemLabelConverter(CONVERTER_FACTORY, selectOne);
	}
	
	@SuppressWarnings("unchecked")
	private Map<String, Object> getItems(Combo combo) {
		Map<String, Object> result = (Map<String, Object>)combo.getData();
		if (result == null) {
			result = new LinkedHashMap<String, Object>();
			combo.setData(result);
		}
		return result;
	}

	private void updateDefaultValue(Combo combo, UISelectOne<Object> selectOne) {
		Converter<Object, String> converter = getConverter(selectOne);
		String value = converter.convert(InputComponents.getValueFor(selectOne));
		if (value == null) {
			combo.setText("");
		} else if (!value.equals(combo.getText())){
			combo.setText(value);
		}
	}

	private void updateValueChoices(Combo combo, UISelectOne<Object> selectOne) {
		Map<String, Object> oldItems = getItems(combo);
		Map<String, Object> newItems = new LinkedHashMap<String, Object>();
		boolean changed = false;
		Iterable<Object> valueChoices = selectOne.getValueChoices();
		Converter<Object, String> converter = getConverter(selectOne);
		if (valueChoices != null) {
			for (Object choice : valueChoices) {
				String itemLabel = converter.convert(choice);
				Object newObject = Proxies.unwrap(choice);
				newItems.put(itemLabel, newObject);
				if (!changed) {
					Object oldObject = oldItems.get(itemLabel);
					if (oldObject == null || !oldObject.equals(newObject)) {
						changed = true;
					}
				}
			}
		} else if (!oldItems.isEmpty()) {
			changed = true;
		}
		if (changed) {
			combo.removeAll();
			combo.setData(newItems);
			for (String label : newItems.keySet()) {
				combo.add(label);
			}
		}
	}
		
	@SuppressWarnings("unchecked")
	private void updateValues(Combo combo, InputComponent<?, Object> input) {
		UISelectOne<Object> selectOne = (UISelectOne<Object>)input;
		updateValueChoices(combo, selectOne);
		updateDefaultValue(combo, selectOne);
	}
	
}
