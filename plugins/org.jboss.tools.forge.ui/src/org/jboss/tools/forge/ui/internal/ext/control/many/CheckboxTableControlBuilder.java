/**
 * Copyright (c) Red Hat, Inc., contributors and others 2013 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.forge.ui.internal.ext.control.many;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.UISelectMany;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.forge.furnace.util.Sets;
import org.jboss.tools.forge.core.furnace.FurnaceService;
import org.jboss.tools.forge.ui.internal.ext.control.ControlBuilder;
import org.jboss.tools.forge.ui.internal.ext.wizards.ForgeWizardPage;

public class CheckboxTableControlBuilder extends ControlBuilder<Table> {

	private static final String SELECT_ALL_BUTTON_DATA_KEY = "selectAllButton";
	private static final String SELECT_NONE_BUTTON_DATA_KEY = "selectNoneButton";

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Table build(final ForgeWizardPage page, final InputComponent<?, ?> input, final String inputName,
			final Composite container) {

		final Group group = new Group(container, SWT.SHADOW_NONE);
		GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
		layoutData.horizontalSpan = 3;
		group.setLayout(new GridLayout());
		group.setLayoutData(layoutData);
		group.setText(InputComponents.getLabelFor(input, false));

		Composite groupPanel = new Composite(group, SWT.NULL);
		groupPanel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		groupPanel.setLayout(new GridLayout(2, false));

		final Table table = new Table(groupPanel, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		GridData tableLayoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
		tableLayoutData.widthHint = 300;
		tableLayoutData.heightHint = 300;
		table.setLayoutData(tableLayoutData);
		table.setToolTipText(input.getDescription());
		final UISelectMany<Object> selectMany = (UISelectMany) input;
		Converter<Object, String> itemLabelConverter = getConverter(selectMany);
		final Set<Object> data = new LinkedHashSet<>();
		// Adding default values in a separate set
		Iterable<Object> defaultValues = selectMany.getValue();
		if (defaultValues != null) {
			for (Object object : defaultValues) {
				String item = itemLabelConverter.convert(object);
				if (item != null)
					data.add(item);
			}
		}
		Iterable<Object> valueChoices = selectMany.getValueChoices();
		if (valueChoices != null) {
			for (Object next : valueChoices) {
				String value = (next == null) ? null : itemLabelConverter.convert(next);
				if (value != null) {
					TableItem item = new TableItem(table, SWT.NONE);
					item.setData(value);
					item.setText(value);
					item.setChecked(data.contains(value));
				}
			}
		}
		table.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (e.item instanceof TableItem && e.detail == SWT.CHECK) {
					TableItem source = (TableItem) e.item;
					if (source != null && source.getData() != null) {
						if (source.getChecked()) {
							data.add(source.getData());
						} else {
							data.remove(source.getData());
						}
					}
					CommandController controller = page.getController();
					controller.setValueFor(inputName, data);
				}
			}
		});

		Composite buttons = new Composite(groupPanel, SWT.NULL);
		buttons.setLayout(new GridLayout(1, true));
		buttons.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER | GridData.VERTICAL_ALIGN_BEGINNING));

		Button selectAllButton = new Button(buttons, SWT.PUSH);
		table.setData(SELECT_ALL_BUTTON_DATA_KEY, selectAllButton);
		selectAllButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		selectAllButton.setText("Select All");
		selectAllButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				for (TableItem item : table.getItems()) {
					if (!item.getChecked()) {
						item.setChecked(true);
						// Notify selection change
						notifySelectionChange(table, item);
					}
				}
			}
		});

		Button selectNoneButton = new Button(buttons, SWT.PUSH);
		table.setData(SELECT_NONE_BUTTON_DATA_KEY, selectNoneButton);
		selectNoneButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		selectNoneButton.setText("Select None");
		selectNoneButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				for (TableItem item : table.getItems()) {
					if (item.getChecked()) {
						item.setChecked(false);
						// Notify selection change
						notifySelectionChange(table, item);
					}
				}
			}
		});
		setEnabled(table, input.isEnabled());
		return table;
	}

	@Override
	public Control[] getModifiableControlsFor(Table control) {
		return new Control[] { control };
	}

	private void notifySelectionChange(final Table table, TableItem item) {
		Event event = new Event();
		event.item = item;
		event.detail = SWT.CHECK;
		table.notifyListeners(SWT.Selection, event);
	}

	@Override
	protected Class<?> getProducedType() {
		return Object.class;
	}

	@Override
	protected String getSupportedInputType() {
		return InputType.CHECKBOX;
	}

	@Override
	protected Class<?>[] getSupportedInputComponentTypes() {
		return new Class<?>[] { UISelectMany.class };
	}

	private static final Set<Table> TABLE_STATUS_CHANGE = Sets.getConcurrentSet();

	@Override
	public void updateState(Table control, InputComponent<?, ?> input) {
		if (!TABLE_STATUS_CHANGE.add(control)) {
			return;
		}
		try {
			super.updateState(control, input);
			updateValues(control, input);
		} finally {
			TABLE_STATUS_CHANGE.remove(control);
		}
	}

	@Override
	public void setEnabled(Table control, boolean enabled) {
		control.setEnabled(enabled);
		Button selectAllButton = (Button) control.getData(SELECT_ALL_BUTTON_DATA_KEY);
		selectAllButton.setEnabled(enabled);
		Button selectNoneButton = (Button) control.getData(SELECT_NONE_BUTTON_DATA_KEY);
		selectNoneButton.setEnabled(enabled);
	}

	@SuppressWarnings("unchecked")
	private void updateValues(Table table, InputComponent<?, ?> input) {
		UISelectMany<Object> selectMany = (UISelectMany<Object>) input;
		Iterable<Object> valueChoices = selectMany.getValueChoices();
		if (valueChoices == null)
			return;
		List<String> newItems = new ArrayList<>();
		Converter<Object, String> converter = getConverter(selectMany);
		for (Object choice : valueChoices) {
			String itemLabel = converter.convert(choice);
			if (itemLabel != null)
				newItems.add(itemLabel);
		}

		int newSize = newItems.size();
		String[] newItemsArray = newItems.toArray(new String[newSize]);
		String[] oldItems = extractData(table.getItems());
		if (Arrays.equals(newItemsArray, oldItems) == false) {
			for (TableItem item : table.getItems()) {
				if (item.getChecked()) {
					item.setChecked(false);
					// Notify selection change
					notifySelectionChange(table, item);
				}
			}
			table.removeAll();
			Set<String> data = getInputValue(selectMany, converter);
			for (String newItem : newItemsArray) {
				TableItem item = new TableItem(table, SWT.NONE);
				item.setData(newItem);
				item.setText(newItem);
				item.setChecked(data.contains(newItem));
			}
		}
	}

	private Set<String> getInputValue(UISelectMany<Object> selectMany, Converter<Object, String> converter) {
		final Set<String> data = new LinkedHashSet<>();
		// Adding default values in a separate set
		Iterable<Object> defaultValues = selectMany.getValue();
		if (defaultValues != null) {
			for (Object object : defaultValues) {
				String item = converter.convert(object);
				if (item != null)
					data.add(item);
			}
		}
		return data;
	}

	private String[] extractData(TableItem[] items) {
		int length = items.length;
		String[] data = new String[length];
		for (int i = 0; i < length; i++) {
			data[i] = items[i].getText();
		}
		return data;
	}

	private Converter<Object, String> getConverter(UISelectMany<Object> selectMany) {
		return InputComponents.getItemLabelConverter(FurnaceService.INSTANCE.getConverterFactory(), selectMany);
	}

}
