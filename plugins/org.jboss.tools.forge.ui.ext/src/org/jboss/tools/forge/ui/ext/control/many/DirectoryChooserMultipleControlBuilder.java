/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.tools.forge.ui.ext.control.many;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.List;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.tools.forge.ui.ext.wizards.ForgeWizardPage;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class DirectoryChooserMultipleControlBuilder extends
		AbstractListButtonControl {

	@Override
	protected void addButtonPressed(ForgeWizardPage page,
			InputComponent<?, ?> input, List containerList) {
		String selectedPath;
		DirectoryDialog dialog = new DirectoryDialog(page.getShell(), SWT.OPEN);
		dialog.setText("Select a directory");
		selectedPath = dialog.open();
		if (selectedPath != null) {
			containerList.add(selectedPath);
			updateItems(input, containerList);
		}
	}

	@Override
	protected Class<?> getProducedType() {
		return File.class;
	}

	@Override
	protected InputType getSupportedInputType() {
		return InputType.DIRECTORY_PICKER;
	}

}
