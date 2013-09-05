/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.tools.forge.ui.ext.control;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Text;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.tools.forge.ui.ext.wizards.ForgeWizardPage;

public class DirectoryChooserControlBuilder extends AbstractTextButtonControl {

	@Override
	protected void browseButtonPressed(ForgeWizardPage page,
			InputComponent<?, Object> input, Text containerText) {
		String selectedPath;
		DirectoryDialog dialog = new DirectoryDialog(page.getShell(), SWT.OPEN);
		dialog.setText("Select a directory");
		dialog.setFilterPath(containerText.getText());
		selectedPath = dialog.open();
		if (selectedPath != null) {
			containerText.setText(selectedPath);
		}
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
		return InputType.DIRECTORY_PICKER;
	}

	@Override
	protected Class<?>[] getSupportedInputComponentTypes() {
		return new Class<?>[] { UIInput.class };
	}
}
