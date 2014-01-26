package org.jboss.tools.forge.ui.actions;

import java.net.URL;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.jboss.tools.forge.ui.ForgeUIPlugin;
import org.jboss.tools.forge.ui.console.ForgeConsole;
import org.jboss.tools.forge.ui.part.ForgeConsoleView;

public class ForgeConsoleShowAction extends Action {
	
	private ForgeConsoleView forgeConsoleView = null;
	private ForgeConsole forgeConsole = null;
	
	public ForgeConsoleShowAction(ForgeConsoleView forgeConsoleView, ForgeConsole forgeConsole) {
		super(forgeConsole.getName(), AS_RADIO_BUTTON);
		this.forgeConsoleView = forgeConsoleView;
		this.forgeConsole = forgeConsole;
		setImageDescriptor(createImageDescriptor());
	}
	
	@Override
	public void run() {
		forgeConsoleView.showForgeConsole(forgeConsole);
	}

	private ImageDescriptor createImageDescriptor() {
		URL url = ForgeUIPlugin.getDefault().getBundle().getEntry("icons/forge.png");
		return ImageDescriptor.createFromURL(url);
	}

}
