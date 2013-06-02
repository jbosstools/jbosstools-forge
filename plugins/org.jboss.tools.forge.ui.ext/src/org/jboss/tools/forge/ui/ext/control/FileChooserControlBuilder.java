/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.tools.forge.ui.ext.control;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.tools.forge.ext.core.FurnaceService;
import org.jboss.tools.forge.ui.ext.wizards.ForgeWizardPage;

public class FileChooserControlBuilder extends ControlBuilder {

	@Override
	public Control build(final ForgeWizardPage page,
			final InputComponent<?, Object> input, final Composite parent) {
		// Create the label
		Label label = new Label(parent, SWT.NULL);
		label.setText(InputComponents.getLabelFor(input, true));

		Composite container = new Composite(parent, SWT.NULL);
		container.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 2;
		layout.verticalSpacing = 9;
		layout.marginWidth = 0;
		layout.marginHeight = 0;

		final Text containerText = new Text(container, SWT.BORDER | SWT.SINGLE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		containerText.setLayoutData(gd);

		// Set Default Value
		final ConverterFactory converterFactory = FurnaceService.INSTANCE
				.getConverterFactory();
		if (converterFactory != null) {
			Converter<Object, String> converter = converterFactory
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
					File file = new File(text);
					InputComponents.setValueFor(converterFactory, input, file);
				}
			}
		});

		Button button = new Button(container, SWT.PUSH);
		button.setText("Browse...");
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO: Check if it is a Directory or a file selection
				boolean directory = true;
				String selectedPath;
				if (directory) {
					DirectoryDialog dialog = new DirectoryDialog(page
							.getShell(), SWT.OPEN);
					dialog.setText("Select a directory");
					dialog.setFilterPath(containerText.getText());
					selectedPath = dialog.open();
				} else {
					FileDialog dialog = new FileDialog(page.getShell(),
							SWT.OPEN);
					dialog.setText("Select a file");
					dialog.setFileName(containerText.getText());
					selectedPath = dialog.open();
				}
				if (selectedPath != null) {
					containerText.setText(selectedPath);

				}
			}
		});
		return container;
	}

	@Override
	public void setEnabled(Control control, boolean enabled) {
		Composite container = (Composite) control;
		for (Control childControl : container.getChildren()) {
			childControl.setEnabled(enabled);
		}
	}

	@Override
	protected Class<File> getProducedType() {
		return File.class;
	}

	@Override
	protected InputType getSupportedInputType() {
		return InputType.FILE_PICKER;
	}

	@Override
	protected Class<?>[] getSupportedInputComponentTypes() {
		return new Class<?>[] { UIInput.class };
	}
}
