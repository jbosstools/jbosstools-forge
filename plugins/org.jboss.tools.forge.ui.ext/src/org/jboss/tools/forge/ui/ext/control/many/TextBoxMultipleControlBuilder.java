/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.tools.forge.ui.ext.control.many;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.List;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.tools.forge.ui.ext.wizards.ForgeWizardPage;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class TextBoxMultipleControlBuilder extends AbstractListButtonControl {

	@Override
	protected void addButtonPressed(ForgeWizardPage page,
			InputComponent<?, ?> input, List containerList) {
		InputDialog dlg = new InputDialog(page.getShell(), "", "Enter a value",
				"", null);
		if (dlg.open() == Window.OK) {
			// User clicked OK; update the label with the input
			containerList.add(dlg.getValue());
			updateItems(input, containerList);
		}
	}

	@Override
	protected Class<String> getProducedType() {
		return String.class;
	}

	@Override
	protected InputType getSupportedInputType() {
		return InputType.TEXTBOX;
	}

}
