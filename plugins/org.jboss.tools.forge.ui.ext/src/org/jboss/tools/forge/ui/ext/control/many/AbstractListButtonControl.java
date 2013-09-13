/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.tools.forge.ui.ext.control.many;

import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.List;
import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.UIInputMany;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.tools.forge.ui.ext.control.ControlBuilder;
import org.jboss.tools.forge.ui.ext.wizards.ForgeWizardPage;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public abstract class AbstractListButtonControl extends ControlBuilder {

	@SuppressWarnings("unchecked")
	@Override
	public Control build(final ForgeWizardPage page,
			final InputComponent<?, Object> input, final Composite container) {

		final Group group = new Group(container, SWT.SHADOW_NONE);
		GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
		layoutData.horizontalSpan = 2;
		group.setLayout(new GridLayout());
		group.setLayoutData(layoutData);
		group.setText(InputComponents.getLabelFor(input, false));
		Composite groupPanel = new Composite(group, SWT.NULL);
		groupPanel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		groupPanel.setLayout(new GridLayout(2, false));

		final List containerList = new List(groupPanel, SWT.MULTI
				| SWT.V_SCROLL | SWT.H_SCROLL);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		containerList.setLayoutData(gd);

		UIInputMany<Object> inputMany = (UIInputMany<Object>) input;

		// Set Default Value
		final ConverterFactory converterFactory = getConverterFactory();
		Converter<Object, String> converter = converterFactory.getConverter(
				input.getValueType(), String.class);
		Iterable<Object> value = inputMany.getValue();
		if (value != null) {
			for (Object item : value) {
				String convertedValue = converter.convert(item);
				if (convertedValue != null) {
					containerList.add(convertedValue);
				}
			}
		}

		containerList.setToolTipText(input.getDescription());
		Composite buttons = new Composite(groupPanel, SWT.NULL);
		buttons.setLayout(new GridLayout(1, true));
		Button addButton = new Button(buttons, SWT.PUSH);
		addButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		addButton.setText("Add...");
		addButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				addButtonPressed(page, input, containerList);
			}
		});

		Button removeButton = new Button(buttons, SWT.PUSH);
		removeButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		removeButton.setText("Remove");
		removeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				removeButtonPressed(page, input, containerList);
			}
		});
		return container;
	}

	protected abstract void addButtonPressed(final ForgeWizardPage page,
			final InputComponent<?, Object> input, final List containerList);

	protected void removeButtonPressed(final ForgeWizardPage page,
			final InputComponent<?, Object> input, final List containerList) {
		containerList.remove(containerList.getSelectionIndices());
		updateItems(input, containerList);
	}

	protected void updateItems(final InputComponent<?, Object> input,
			final List containerList) {
		InputComponents.setValueFor(getConverterFactory(), input,
				Arrays.asList(containerList.getItems()));
	}

	@Override
	protected Class<?>[] getSupportedInputComponentTypes() {
		return new Class<?>[] { UIInputMany.class };
	}
}
