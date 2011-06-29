package org.jboss.tools.forge.core.preferences;

import static org.junit.Assert.assertEquals;

import org.jboss.tools.forge.core.process.ForgeEmbeddedRuntime;
import org.junit.Test;

public class ForgeRuntimesPreferencesTest {
	
	private static final String ALTERNATIVE_FORGE_RUNTIMES = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
			"<forgeRuntimes default=\"foo\">" +
			"   <runtime name=\"embedded\" type=\"embedded\"/>" +
			"   <runtime name=\"foo\" location=\"foofoo\" type=\"external\"/>" +
			"   <runtime name=\"bar\" location=\"barbar\" type=\"external\"/>" +
			"</forgeInstallations>";
	
	@Test
	public void testGetDefaultInitialCase() {
		assertEquals(ForgeEmbeddedRuntime.INSTANCE, ForgeRuntimesPreferences.INSTANCE.getDefault());
	}
	
	@Test
	public void testGetRuntimesInitialCase() {
		assertEquals(1, ForgeRuntimesPreferences.INSTANCE.getRuntimes().length);
	}
	
//	@Test
//	public void testGetDefaultAlternativeCase() {
//		ForgeInstallations.INSTANCE.defaultInstallation = null;
//		InstanceScope.INSTANCE.getNode(ForgeCorePlugin.PLUGIN_ID).put(
//				ForgeInstallations.PREF_FORGE_INSTALLATIONS, 
//				ALTERNATIVE_FORGE_INSTALLATIONS);
//		ForgeRuntime runtime = ForgeInstallations.INSTANCE.getDefault();
//		assertNotNull(ForgeInstallations.INSTANCE.defaultInstallation);
//		assertEquals("foo", runtime.getName());
//	}
//	
//	@Test
//	public void testGetInstallationsAlternativeCase() {
//		ForgeInstallations.INSTANCE.installations = null;
//		InstanceScope.INSTANCE.getNode(ForgeCorePlugin.PLUGIN_ID).put(
//				ForgeInstallations.PREF_FORGE_INSTALLATIONS, 
//				ALTERNATIVE_FORGE_INSTALLATIONS);
//		ForgeRuntime[] runtimes = ForgeInstallations.INSTANCE.getInstallations();
//		assertNotNull(ForgeInstallations.INSTANCE.installations);
//		assertEquals(3, runtimes.length);
//	}
	
	
	
	
}
