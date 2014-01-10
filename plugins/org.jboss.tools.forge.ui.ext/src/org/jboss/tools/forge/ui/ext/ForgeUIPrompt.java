/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.tools.forge.ui.ext;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.jboss.forge.addon.ui.input.UIPrompt;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ForgeUIPrompt implements UIPrompt {
	private final Shell shell;

	public ForgeUIPrompt(Shell shell) {
		this.shell = shell;
	}

	@Override
	public String prompt(String message) {
		InputDialog dlg = new InputDialog(shell, "", message, "", null);
		return (dlg.open() == Window.OK) ? dlg.getValue() : null;
	}

	@Override
	public boolean promptBoolean(String message) {
		return MessageDialog.openQuestion(shell, "Question", message);
	}

	@Override
	public String promptSecret(String message) {
		// FIXME: Should mask the input
		InputDialog dlg = new InputDialog(shell, "", message, "", null);
		return (dlg.open() == Window.OK) ? dlg.getValue() : null;
	}

}
