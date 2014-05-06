package org.jboss.tools.forge.ui.internal.actions;

import java.net.URL;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.jboss.tools.forge.core.runtime.ForgeRuntime;
import org.jboss.tools.forge.core.runtime.ForgeRuntimeState;
import org.jboss.tools.forge.ui.internal.ForgeUIPlugin;
import org.jboss.tools.forge.ui.util.ForgeHelper;

public class StartAction extends Action {
	
	ForgeRuntime runtime;

	public StartAction(ForgeRuntime runtime) {
		super();
		this.runtime = runtime;
		setImageDescriptor(createImageDescriptor());
	}

	@Override
	public void run() {
		if (ForgeRuntimeState.RUNNING.equals(runtime.getState())) return;
		ForgeHelper.start(runtime);
	}
	
	@Override
	public boolean isEnabled() {
		return ForgeRuntimeState.STOPPED.equals(runtime.getState());
	}

	private ImageDescriptor createImageDescriptor() {
		URL url = ForgeUIPlugin.getDefault().getBundle().getEntry("icons/start.gif");
		return ImageDescriptor.createFromURL(url);
	}

}
