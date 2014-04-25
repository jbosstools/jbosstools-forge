/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.tools.forge.ui.ext.dialog;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.jboss.forge.addon.ui.controller.WizardCommandController;
import org.jboss.tools.forge.ui.ForgeUIPlugin;
import org.jboss.tools.forge.ui.notifications.NotificationType;

/**
 * Used when the controller is a Wizard
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ForgeWizardDialog extends WizardDialog {

	private final WizardCommandController controller;

	public ForgeWizardDialog(Shell parentShell, IWizard newWizard,
			WizardCommandController controller) {
		super(parentShell, newWizard);
		this.controller = controller;
	}

	@Override
	protected void backPressed() {
		try {
			controller.previous();
			super.backPressed();
		} catch (Exception e) {
			ForgeUIPlugin
					.displayMessage(
							"Error",
							"Error while navigating to the previous page, check Error Log view",
							NotificationType.ERROR);
			ForgeUIPlugin.log(e);
		}
	}

	@Override
	protected void nextPressed() {
		try {
			controller.next();
			super.nextPressed();
		} catch (Exception e) {
			ForgeUIPlugin
					.displayMessage(
							"Error",
							"Error while navigating to the next page, check Error Log view",
							NotificationType.ERROR);
			ForgeUIPlugin.log(e);
		}
	}

}
