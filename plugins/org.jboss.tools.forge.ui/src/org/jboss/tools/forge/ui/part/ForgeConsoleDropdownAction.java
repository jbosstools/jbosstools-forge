package org.jboss.tools.forge.ui.part;

import java.net.URL;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.jboss.tools.forge.ui.ForgeUIPlugin;

public class ForgeConsoleDropdownAction extends Action implements IMenuCreator {
	
	public ForgeConsoleDropdownAction() {
		setImageDescriptor(createImageDescriptor());
	}

	@Override
	public void dispose() {
	}

	@Override
	public Menu getMenu(Control arg0) {
		return null;
	}

	@Override
	public Menu getMenu(Menu arg0) {
		return null;
	}
	
	private ImageDescriptor createImageDescriptor() {
		URL url = ForgeUIPlugin.getDefault().getBundle().getEntry("icons/jbossforge_icon_16px.png");
		return ImageDescriptor.createFromURL(url);
	}

}
