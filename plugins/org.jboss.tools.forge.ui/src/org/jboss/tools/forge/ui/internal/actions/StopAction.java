/**
 * Copyright (c) Red Hat, Inc., contributors and others 2013 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.forge.ui.internal.actions;

import java.net.URL;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.jboss.tools.forge.core.runtime.ForgeRuntime;
import org.jboss.tools.forge.core.runtime.ForgeRuntimeState;
import org.jboss.tools.forge.ui.internal.ForgeUIPlugin;
import org.jboss.tools.forge.ui.util.ForgeHelper;

public class StopAction extends Action {
	
	ForgeRuntime runtime;

	public StopAction(ForgeRuntime runtime) {
		super();
		this.runtime = runtime;
		setImageDescriptor(createImageDescriptor());
		setToolTipText("Stop " + runtime.getName());
	}

	@Override
	public void run() {
		if (ForgeRuntimeState.STOPPED.equals(runtime.getState())) return;
		ForgeHelper.stop(runtime);
	}
	
	@Override
	public boolean isEnabled() {
		return ForgeRuntimeState.RUNNING.equals(runtime.getState());
	}

	private ImageDescriptor createImageDescriptor() {
		URL url = ForgeUIPlugin.getDefault().getBundle().getEntry("icons/stop.gif");
		return ImageDescriptor.createFromURL(url);
	}

}
