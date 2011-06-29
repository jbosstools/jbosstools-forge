package org.jboss.tools.forge.core.preferences;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.jboss.tools.forge.core.ForgeCorePlugin;
import org.junit.Test;

public class ForgePreferenceInitializerTest {
	
	@Test
	public void testInitializeDefaultPreferences() {
		IEclipsePreferences preferences = InstanceScope.INSTANCE.getNode(ForgeCorePlugin.PLUGIN_ID);
		assertNull(preferences.get(ForgeRuntimesPreferences.PREF_FORGE_RUNTIMES, null));
		new ForgePreferenceInitializer().initializeDefaultPreferences();
		assertEquals(
				ForgePreferenceInitializer.INITIAL_RUNTIMES_PREFERENCE, 
				preferences.get(ForgeRuntimesPreferences.PREF_FORGE_RUNTIMES, null));
	}

}
