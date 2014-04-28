package org.jboss.tools.forge.ui.internal.console;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.jboss.tools.forge.core.runtime.ForgeRuntime;
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
        		ForgeConsole forgeConsole = (ForgeConsole)element.createExecutableExtension("class");
        		int index = calculateIndex(forgeConsole);
				consoles.add(index, forgeConsole);
			} catch (CoreException e) {
				ForgeUIPlugin.log(e);
			}
        }
		
	}
	
	public ForgeConsole[] getConsoles() {
		return consoles.toArray(new ForgeConsole[consoles.size()]);
	}
	
	private int calculateIndex(ForgeConsole forgeConsole) {
		ForgeRuntime runtime = forgeConsole.getRuntime();
		String newVersion = runtime.getVersion();
		for (int i = 0; i < consoles.size(); i++) {
			String version = consoles.get(i).getRuntime().getVersion();
			if (isNewer(newVersion, version)) {
				return i;
			}
		}
		return consoles.size();
	}
	
	private boolean isNewer(String first, String second) {
		String[] firstValues = first.split("\\.|-");
		String[] secondValues = second.split("\\.|-");
		for (int i = 0; i < 2; i++) {
			if (Integer.valueOf(firstValues[i]) > Integer.valueOf(secondValues[i])) {
				return true;
			}
		}
		return "Final".equals(firstValues[3]);
	}

}
