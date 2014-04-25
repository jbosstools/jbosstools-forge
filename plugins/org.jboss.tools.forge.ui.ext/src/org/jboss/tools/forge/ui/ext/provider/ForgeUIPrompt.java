/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.tools.forge.ui.ext.provider;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.jboss.forge.addon.ui.input.UIPrompt;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ForgeUIPrompt implements UIPrompt {
	private final Shell shell;
	private boolean booleanResult = false;
	private String stringResult = null;

	public ForgeUIPrompt(Shell shell) {
		this.shell = shell;
	}

	@Override
	public String prompt(final String message) {
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				InputDialog dlg = new InputDialog(shell, "", message, "", null);
				stringResult = (dlg.open() == Window.OK) ? dlg.getValue()
						: null;
			}

		});
		return stringResult;
	}

	@Override
	public boolean promptBoolean(final String message) {
		return promptBoolean(message, true);
	}

	@Override
	public String promptSecret(final String message) {
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				// FIXME: Should mask the input
				InputDialog dlg = new InputDialog(shell, "", message, "", null);
				stringResult = (dlg.open() == Window.OK) ? dlg.getValue()
						: null;
			}
		});
		return stringResult;
	}

	@Override
	public boolean promptBoolean(final String message,
			final boolean defaultValue) {
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				booleanResult = MessageDialog.openQuestion(shell, "Question",
						message);
			}
		});
		return booleanResult;
	}
}
