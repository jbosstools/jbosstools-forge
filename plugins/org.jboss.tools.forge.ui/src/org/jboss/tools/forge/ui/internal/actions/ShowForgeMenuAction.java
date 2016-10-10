/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.tools.forge.ui.internal.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchWindow;
import org.jboss.forge.furnace.util.OperatingSystemUtils;
import org.jboss.tools.forge.ui.internal.ForgeUIPlugin;
import org.jboss.tools.forge.ui.internal.ext.handlers.ForgeCommandHandler;

/**
 * Adds a new menu entry in the Forge Console view that triggers the Forge
 * wizard dialog
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class ShowForgeMenuAction extends Action {
	private final IWorkbenchWindow window;

	public ShowForgeMenuAction(IWorkbenchWindow window) {
		this.window = window;
		setText("Open Wizard Menu...");
		setToolTipText("Displays the Quick Access Forge Wizard Menu");
		// It would be easier if SWT.MOD1 could be used
		int modifierKey = OperatingSystemUtils.isOSX() ? SWT.COMMAND : SWT.CTRL;
		setAccelerator(modifierKey | '4');
		setImageDescriptor(ForgeUIPlugin.getForgeIcon());
	}

	@Override
	public void run() {
		new ForgeCommandHandler().openWizardDialog(window);
	}
}
