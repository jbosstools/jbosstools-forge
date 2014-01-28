package org.jboss.tools.forge.ui.actions.f1;

import java.net.URL;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.jboss.tools.forge.ui.ForgeUIPlugin;
import org.jboss.tools.forge.ui.util.ForgeHelper;

public class GoToAction extends Action {

	public GoToAction() {
		super();
		setImageDescriptor(createImageDescriptor());
	}

	@Override
	public void run() {
		System.out.println("clicked goto");
	}
	
	public boolean isEnabled() {
		return ForgeHelper.isForgeRunning();
	}

	private ImageDescriptor createImageDescriptor() {
		URL url = ForgeUIPlugin.getDefault().getBundle().getEntry("icons/goto_obj.gif");
		return ImageDescriptor.createFromURL(url);
	}

}
