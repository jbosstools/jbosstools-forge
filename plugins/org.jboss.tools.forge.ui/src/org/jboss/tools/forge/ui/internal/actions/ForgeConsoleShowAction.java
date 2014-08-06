/**
 * Copyright (c) Red Hat, Inc., contributors and others 2004 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.forge.ui.internal.actions;

import java.net.URL;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.jboss.tools.forge.ui.internal.ForgeUIPlugin;
import org.jboss.tools.forge.ui.internal.console.ForgeConsole;
import org.jboss.tools.forge.ui.internal.part.ForgeConsoleView;

public class ForgeConsoleShowAction extends Action {
	
	private ForgeConsoleView forgeConsoleView = null;
	private ForgeConsole forgeConsole = null;
	
	public ForgeConsoleShowAction(ForgeConsoleView forgeConsoleView, ForgeConsole forgeConsole) {
		super(forgeConsole.getLabel(), AS_RADIO_BUTTON);
		this.forgeConsoleView = forgeConsoleView;
		this.forgeConsole = forgeConsole;
		setImageDescriptor(createImageDescriptor());
	}
	
	@Override
	public void run() {
		forgeConsoleView.showForgeConsole(forgeConsole);
	}

	private ImageDescriptor createImageDescriptor() {
		URL url = ForgeUIPlugin.getDefault().getBundle().getEntry("icons/forge.png");
		return ImageDescriptor.createFromURL(url);
	}

}
