package org.jboss.tools.forge.ui.ext.actions;

import java.net.URL;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.jboss.tools.forge.ext.core.ForgeCorePlugin;
import org.jboss.tools.forge.ui.ext.ForgeUIPlugin;
import org.jboss.tools.forge.ui.ext.util.FurnaceHelper;

public class StartF2Action extends Action {

	public StartF2Action() {
		super();
		setImageDescriptor(createImageDescriptor());
	}

	@Override
	public void run() {
		FurnaceHelper.startFurnace();
	}
	
	@Override
	public boolean isEnabled() {
		return !ForgeCorePlugin.getDefault().isFurnaceStarted();
	}

	private ImageDescriptor createImageDescriptor() {
		URL url = ForgeUIPlugin.getDefault().getBundle().getEntry("icons/start.gif");
		return ImageDescriptor.createFromURL(url);
	}

}
