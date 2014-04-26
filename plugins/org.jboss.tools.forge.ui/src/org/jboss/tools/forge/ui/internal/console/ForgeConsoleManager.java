package org.jboss.tools.forge.ui.internal.console;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.jboss.tools.forge.ui.internal.ForgeUIPlugin;

public class ForgeConsoleManager {
	
	public static final ForgeConsoleManager INSTANCE = new ForgeConsoleManager();
	private List<ForgeConsole> consoles = new ArrayList<ForgeConsole>();
	
	private ForgeConsoleManager() {
		createConsoles();
	}
	
	private void createConsoles() {
        IExtensionPoint extensionPoint = 
        		Platform.getExtensionRegistry().getExtensionPoint(
        				"org.jboss.tools.forge.ui.consoles");
        for (IConfigurationElement element : extensionPoint.getConfigurationElements()) {
        	try {
				consoles.add((ForgeConsole)element.createExecutableExtension("class"));
			} catch (CoreException e) {
				ForgeUIPlugin.log(e);
			}
        }
		
	}
	
	public ForgeConsole[] getConsoles() {
		return consoles.toArray(new ForgeConsole[consoles.size()]);
	}

}
