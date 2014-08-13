/**
 * Copyright (c) Red Hat, Inc., contributors and others 2014. All rights reserved
 *
 * Contributors:
 *     George Gastaldi (Red Hat, Inc.)
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.forge.ui.internal.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsoleConstants;
import org.jboss.tools.forge.core.runtime.ForgeRuntime;
import org.jboss.tools.forge.core.runtime.ForgeRuntimeState;
import org.jboss.tools.forge.ui.internal.console.ForgeConsole;
import org.jboss.tools.forge.ui.internal.console.ForgeConsoleManager;

/**
 * Invoked when the Clear Console icon is pressed
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ClearAction extends Action {

	private ForgeRuntime runtime;

	public ClearAction(ForgeRuntime runtime) {
		this.runtime = runtime;
		setImageDescriptor(createImageDescriptor());
		setToolTipText("Clear Console");
	}

	@Override
	public void run() {
		ForgeConsole console = ForgeConsoleManager.INSTANCE.getConsole(runtime);
		console.clear();
	}

	@Override
	public boolean isEnabled() {
		return ForgeRuntimeState.RUNNING.equals(runtime.getState());
	}

	private ImageDescriptor createImageDescriptor() {
		Image img = ConsolePlugin.getImage(IConsoleConstants.IMG_LCL_CLEAR);
		return ImageDescriptor.createFromImage(img);
	}
}
