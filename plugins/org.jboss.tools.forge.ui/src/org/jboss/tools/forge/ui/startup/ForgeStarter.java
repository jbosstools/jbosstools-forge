package org.jboss.tools.forge.ui.startup;

import org.eclipse.ui.IStartup;
import org.jboss.tools.forge.core.ForgeCorePlugin;

public class ForgeStarter implements IStartup {

	@Override
	public void earlyStartup() {
		// reference the plugin class to start the Forge 2 runtime
		ForgeCorePlugin.getDefault();
	}

}
