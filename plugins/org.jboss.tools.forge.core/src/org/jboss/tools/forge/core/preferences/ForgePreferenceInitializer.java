package org.jboss.tools.forge.core.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.jboss.tools.forge.core.ForgeCorePlugin;

public class ForgePreferenceInitializer extends AbstractPreferenceInitializer {

	static final String INITIAL_INSTALLATIONS_PREFERENCE =
			"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
			"<forgeInstallations default=\"embedded\">" +
			"   <installation name=\"embedded\"/>" +
			"</forgeInstallations>";

	@Override
	public void initializeDefaultPreferences() {
		IEclipsePreferences preferences = InstanceScope.INSTANCE.getNode(ForgeCorePlugin.PLUGIN_ID);
		preferences.put(ForgeInstallations.PREF_FORGE_INSTALLATIONS, INITIAL_INSTALLATIONS_PREFERENCE);
	}

}
