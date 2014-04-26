/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.tools.forge.ui.internal.ext.control;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.tools.forge.core.furnace.FurnaceService;
import org.jboss.tools.forge.ui.internal.ext.wizards.ForgeWizardPage;

public abstract class AbstractTextButtonControl extends ControlBuilder<Control> {

	@SuppressWarnings("unchecked")
	@Override
	public Control build(final ForgeWizardPage page,
			final InputComponent<?, ?> input, final String inputName,
			final Composite parent) {
		// Create the label
		Label label = new Label(parent, SWT.NULL);
		label.setText(getMnemonicLabel(input, true));

		final Text containerText = new Text(parent, SWT.BORDER | SWT.SINGLE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		containerText.setLayoutData(gd);

		// Set Default Value
		final ConverterFactory converterFactory = FurnaceService.INSTANCE
				.getConverterFactory();
		if (converterFactory != null) {
			Converter<Object, String> converter = (Converter<Object, String>) converterFactory
					.getConverter(input.getValueType(), String.class);
			String value = converter
					.convert(InputComponents.getValueFor(input));
			containerText.setText(value == null ? "" : value);
		}
		containerText.setToolTipText(input.getDescription());
		containerText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				String text = containerText.getText();
				if (text != null) {
					final CommandController controller = page.getController();
					controller.setValueFor(inputName, text);
				}
			}
		});
		decorateContainerText(page, input, containerText);
		Button button = new Button(parent, SWT.PUSH);
		button.setText("Browse...");
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				browseButtonPressed(page, input, containerText);
			}
		});
		setupAutoCompleteForText(page.getWizard().getUIContext(), input,
				InputComponents.getCompleterFor(input), containerText);
		return containerText;
	}

	/**
	 * Ugly workaround because the Browse button is not exposed to the caller
	 * class
	 *
	 * TODO: Refactor ControlBuilder
	 */
	@Override
	public void setEnabled(Control control, boolean enabled) {
		control.setEnabled(enabled);
		// Enable/disable Browse button
		Composite parent = control.getParent();
		Control[] children = parent.getChildren();
		for (int i = 0; i < children.length; i++) {
			if (children[i] == control) {
				children[i + 1].setEnabled(enabled);
				break;
			}
		}
	}

	protected void decorateContainerText(final ForgeWizardPage page,
			final InputComponent<?, ?> input, final Text containerText) {

	}

	protected abstract void browseButtonPressed(final ForgeWizardPage page,
			final InputComponent<?, ?> input, final Text containerText);

}
