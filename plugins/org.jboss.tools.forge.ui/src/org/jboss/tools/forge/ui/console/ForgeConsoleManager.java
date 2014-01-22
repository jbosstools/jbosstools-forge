package org.jboss.tools.forge.ui.console;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;

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
        	System.out.println("found an element: " + element.getName());
        }
		
	}
	
	public ForgeConsole[] getConsoles() {
		return consoles.toArray(new ForgeConsole[consoles.size()]);
	}

}
