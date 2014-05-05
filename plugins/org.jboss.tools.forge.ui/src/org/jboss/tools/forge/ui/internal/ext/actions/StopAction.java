package org.jboss.tools.forge.ui.internal.ext.actions;

import java.net.URL;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.jboss.tools.forge.core.furnace.FurnaceService;
import org.jboss.tools.forge.core.runtime.ForgeRuntime;
import org.jboss.tools.forge.core.runtime.ForgeRuntimeState;
import org.jboss.tools.forge.ui.internal.ForgeUIPlugin;
import org.jboss.tools.forge.ui.internal.ext.util.FurnaceHelper;

public class StopAction extends Action {
	
	ForgeRuntime runtime;

	public StopAction(ForgeRuntime runtime) {
		super();
		this.runtime = runtime;
		setImageDescriptor(createImageDescriptor());
	}

	@Override
	public void run() {
		if (ForgeRuntimeState.STOPPED.equals(runtime.getState())) return;
		FurnaceHelper.createStopRuntimeJob(runtime).schedule();
	}
	
	@Override
	public boolean isEnabled() {
		return FurnaceService.INSTANCE.getContainerStatus().isStarted();
	}

	private ImageDescriptor createImageDescriptor() {
		URL url = ForgeUIPlugin.getDefault().getBundle().getEntry("icons/stop.gif");
		return ImageDescriptor.createFromURL(url);
	}

}
