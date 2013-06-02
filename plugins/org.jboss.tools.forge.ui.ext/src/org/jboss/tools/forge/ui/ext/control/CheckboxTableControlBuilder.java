/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.tools.forge.ui.ext.control;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.UISelectMany;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.forge.proxy.Proxies;
import org.jboss.tools.forge.ext.core.FurnaceService;
import org.jboss.tools.forge.ui.ext.wizards.ForgeWizardPage;

public class CheckboxTableControlBuilder extends ControlBuilder {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Control build(ForgeWizardPage page,
			final InputComponent<?, Object> input, final Composite container) {

		final Group group = new Group(container, SWT.SHADOW_NONE);
		GridData layoutData = new GridData(GridData.FILL_BOTH);
		layoutData.horizontalSpan = 2;
		group.setLayout(new GridLayout());
		group.setLayoutData(layoutData);
		group.setText(InputComponents.getLabelFor(input, false));

		final Table table = new Table(group, SWT.CHECK | SWT.BORDER
				| SWT.V_SCROLL | SWT.H_SCROLL);
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		table.setToolTipText(input.getDescription());
		final UISelectMany<Object> selectMany = (UISelectMany) input;
		final ConverterFactory converterFactory = FurnaceService.INSTANCE
				.getConverterFactory();
		final List<Object> data = new ArrayList<Object>();
		Iterable<Object> valueChoices = selectMany.getValueChoices();
		// Adding default values in a separate set
		Set<Object> defaultValuesSet = new HashSet<Object>();
		Iterable<Object> defaultValues = selectMany.getValue();
		if (defaultValues != null) {
			for (Object object : defaultValues) {
				defaultValuesSet.add(object);
			}
		}
		if (valueChoices != null) {
			for (Object next : valueChoices) {
				TableItem item = new TableItem(table, SWT.NONE);
				item.setData(Proxies.unwrap(next));
				item.setText(next.toString());
				item.setChecked(defaultValuesSet.contains(next));
			}
		}
		table.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (e.item instanceof TableItem) {
					TableItem source = (TableItem) e.item;
					if (source.getChecked()) {
						data.add(source.getData());
					} else {
						data.remove(source.getData());
					}
					InputComponents.setValueFor(converterFactory, input, data);
				}
			}
		});
		return table;
	}

	@Override
	protected Class<?> getProducedType() {
		return Object.class;
	}

	@Override
	protected InputType getSupportedInputType() {
		return InputType.SELECT_MANY_CHECKBOX;
	}

	@Override
	protected Class<?>[] getSupportedInputComponentTypes() {
		return new Class<?>[] { UISelectMany.class };
	}
}
