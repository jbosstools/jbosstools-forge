package org.jboss.tools.forge.ui.ext.actions;

import java.net.URL;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.jboss.tools.forge.ext.core.FurnaceService;
import org.jboss.tools.forge.ui.ext.ForgeUIPlugin;
import org.jboss.tools.forge.ui.ext.util.FurnaceHelper;

public class StopAction extends Action {

	public StopAction() {
		super();
		setImageDescriptor(createImageDescriptor());
	}

	@Override
	public void run() {
		FurnaceHelper.stopFurnace();
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
