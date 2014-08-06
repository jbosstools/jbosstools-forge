/**
 * Copyright (c) Red Hat, Inc., contributors and others 2013 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
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
