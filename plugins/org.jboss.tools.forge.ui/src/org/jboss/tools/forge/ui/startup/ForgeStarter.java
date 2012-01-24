package org.jboss.tools.forge.ui.startup;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IStartup;
import org.jboss.tools.forge.core.preferences.ForgeRuntimesPreferences;
import org.jboss.tools.forge.ui.util.ForgeHelper;

public class ForgeStarter implements IStartup {

	@Override
	public void earlyStartup() {
		if (ForgeRuntimesPreferences.INSTANCE.getStartup()) {
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					ForgeHelper.startForge();
				}				
			});
		}
	}
	
	
	
}
