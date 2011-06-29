package org.jboss.tools.forge.core.preferences;

import static org.junit.Assert.assertEquals;

import org.jboss.tools.forge.core.process.ForgeEmbeddedRuntime;
import org.junit.Test;

public class ForgeInstallationsTest {
	
	private static final String ALTERNATIVE_FORGE_INSTALLATIONS = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
			"<forgeInstallations default=\"foo\">" +
			"   <installation name=\"embedded\" type=\"embedded\"/>" +
			"   <installation name=\"foo\" location=\"foofoo\" type=\"external\"/>" +
			"   <installation name=\"bar\" location=\"barbar\" type=\"external\"/>" +
			"</forgeInstallations>";
	
	@Test
	public void testGetDefaultInitialCase() {
		assertEquals(ForgeEmbeddedRuntime.INSTANCE, ForgeInstallations.INSTANCE.getDefault());
	}
	
	@Test
	public void testGetInstallationsInitialCase() {
		assertEquals(1, ForgeInstallations.INSTANCE.getInstallations().length);
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
