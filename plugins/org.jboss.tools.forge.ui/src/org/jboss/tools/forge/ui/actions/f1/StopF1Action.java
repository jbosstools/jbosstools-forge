package org.jboss.tools.forge.ui.actions.f1;

import java.net.URL;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.jboss.tools.forge.ui.ForgeUIPlugin;
import org.jboss.tools.forge.ui.util.ForgeHelper;

public class StopF1Action extends Action {

	public StopF1Action() {
		super();
		setImageDescriptor(createImageDescriptor());
	}

	@Override
	public void run() {
		ForgeHelper.stopForge();
	}
	
	@Override
	public boolean isEnabled() {
		return ForgeHelper.isForgeRunning();
	}

	private ImageDescriptor createImageDescriptor() {
		URL url = ForgeUIPlugin.getDefault().getBundle().getEntry("icons/stop.gif");
		return ImageDescriptor.createFromURL(url);
	}

}
