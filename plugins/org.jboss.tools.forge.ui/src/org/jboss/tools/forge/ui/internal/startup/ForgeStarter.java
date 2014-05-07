package org.jboss.tools.forge.ui.internal.startup;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IStartup;
import org.jboss.tools.forge.core.preferences.ForgeCorePreferences;
import org.jboss.tools.forge.ui.util.ForgeHelper;

public class ForgeStarter implements IStartup {

	@Override
	public void earlyStartup() {
		if (ForgeCorePreferences.INSTANCE.getStartup()) {
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					ForgeHelper.start(ForgeCorePreferences.INSTANCE.getDefaultRuntime());
				}				
			});
		}
	}
	
	
	
}
