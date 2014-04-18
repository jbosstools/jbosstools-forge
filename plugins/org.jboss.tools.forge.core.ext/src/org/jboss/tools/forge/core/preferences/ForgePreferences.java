/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.tools.forge.core.preferences;

import java.io.File;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.jboss.forge.furnace.util.OperatingSystemUtils;
import org.jboss.tools.forge.core.ForgeCorePlugin;
import org.osgi.service.prefs.BackingStoreException;

/**
 * Stores the preferences for the core ext module
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
public enum ForgePreferences {
    INSTANCE;

    private static final String PREF_FORGE_ADDON_DIR = "org.jboss.tools.forge.ext.core.addon_dir";

    public String getAddonDir() {
	IEclipsePreferences prefs = getForgeCorePreferences();
	return prefs.get(PREF_FORGE_ADDON_DIR,
		new File(OperatingSystemUtils.getUserForgeDir(), "addons")
			.getAbsolutePath());
    }

    public void setAddonDir(String addonDir) {
	IEclipsePreferences prefs = getForgeCorePreferences();
	prefs.put(PREF_FORGE_ADDON_DIR, addonDir);
	try {
	    prefs.flush();
	} catch (BackingStoreException bse) {
	    ForgeCorePlugin.log(bse);
	}
    }

    private IEclipsePreferences getForgeCorePreferences() {
	return InstanceScope.INSTANCE.getNode(ForgeCorePlugin.PLUGIN_ID);
    }

}
