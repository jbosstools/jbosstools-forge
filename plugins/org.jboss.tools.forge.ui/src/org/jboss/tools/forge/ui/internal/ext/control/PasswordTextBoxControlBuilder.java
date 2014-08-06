/**
 * Copyright (c) Red Hat, Inc., contributors and others 2004 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.forge.ui.internal.ext.control;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.tools.forge.ui.internal.ext.wizards.ForgeWizardPage;

public class PasswordTextBoxControlBuilder extends TextBoxControlBuilder {

	@Override
	public Text build(ForgeWizardPage page,
			final InputComponent<?, ?> input, String inputName, final Composite container) {
		Text txt = super.build(page, input, inputName, container);
		txt.setEchoChar('*');
		return txt;
	}

	@Override
	protected String getSupportedInputType() {
		return InputType.SECRET;
	}
}
