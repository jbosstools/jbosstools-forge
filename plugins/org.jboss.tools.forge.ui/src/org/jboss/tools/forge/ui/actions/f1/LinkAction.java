package org.jboss.tools.forge.ui.actions.f1;

import java.net.URL;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.jboss.tools.forge.ui.ForgeUIPlugin;
import org.jboss.tools.forge.ui.util.ForgeHelper;

public class LinkAction extends Action {

	public LinkAction() {
		super();
		setImageDescriptor(createImageDescriptor());
	}

	@Override
	public void run() {
		System.out.println("clicked link");
	}
	
	@Override
	public boolean isEnabled() {
		return ForgeHelper.isForgeRunning();
	}

	private ImageDescriptor createImageDescriptor() {
		URL url = ForgeUIPlugin.getDefault().getBundle().getEntry("icons/link.gif");
		return ImageDescriptor.createFromURL(url);
	}

}
