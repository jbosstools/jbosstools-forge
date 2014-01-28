package org.jboss.tools.forge.ui.ext.actions;

import java.net.URL;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.jboss.tools.forge.ext.core.ForgeCorePlugin;
import org.jboss.tools.forge.ui.ext.ForgeUIPlugin;

public class StopF2Action extends Action {

	public StopF2Action() {
		super();
		setImageDescriptor(createImageDescriptor());
	}

	@Override
	public void run() {
		ForgeCorePlugin.getDefault().stopFurnace();
	}
	
	@Override
	public boolean isEnabled() {
		return ForgeCorePlugin.getDefault().isFurnaceStarted();
	}

	private ImageDescriptor createImageDescriptor() {
		URL url = ForgeUIPlugin.getDefault().getBundle().getEntry("icons/stop.gif");
		return ImageDescriptor.createFromURL(url);
	}

}
