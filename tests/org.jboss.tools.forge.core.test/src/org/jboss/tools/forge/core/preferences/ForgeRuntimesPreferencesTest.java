package org.jboss.tools.forge.core.preferences;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.jboss.tools.forge.core.ForgeCorePlugin;
import org.jboss.tools.forge.core.process.ForgeEmbeddedRuntime;
import org.jboss.tools.forge.core.process.ForgeExternalRuntime;
import org.jboss.tools.forge.core.process.ForgeRuntime;
import org.junit.Test;

public class ForgeRuntimesPreferencesTest {
	
	private static final String ALTERNATIVE_FORGE_RUNTIMES = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
			"<forgeRuntimes default=\"foo\">" +
			"   <runtime name=\"embedded\" type=\"embedded\"/>" +
			"   <runtime name=\"foo\" location=\"foofoo\" type=\"external\"/>" +
			"   <runtime name=\"bar\" location=\"barbar\" type=\"external\"/>" +
			"</forgeRuntimes>";
	
	@Test
	public void testGetDefaultInitialCase() {
		assertEquals(ForgeEmbeddedRuntime.INSTANCE, ForgeRuntimesPreferences.INSTANCE.getDefault());
	}
	
	@Test
	public void testGetRuntimesInitialCase() {
		assertEquals(1, ForgeRuntimesPreferences.INSTANCE.getRuntimes().length);
	}
	
	@Test
	public void testSetRuntimes() {
		ForgeRuntime[] runtimes = new ForgeRuntime[3];
		runtimes[0] = ForgeEmbeddedRuntime.INSTANCE;
		runtimes[1] = new ForgeExternalRuntime("foo", "foofoo");
		runtimes[2] = new ForgeExternalRuntime("bar", "barbar");
		ForgeRuntimesPreferences.INSTANCE.setRuntimes(runtimes, runtimes[1]);
		assertArrayEquals(runtimes, ForgeRuntimesPreferences.INSTANCE.getRuntimes());
		assertEquals(runtimes[1], ForgeRuntimesPreferences.INSTANCE.getDefault());
		verifyForgeRuntimesPreferencesString();
	}
	
	private void verifyForgeRuntimesPreferencesString() {
		String forgeRuntimesPrefs = 
				InstanceScope.INSTANCE.getNode(ForgeCorePlugin.PLUGIN_ID).get(
						ForgeRuntimesPreferences.PREF_FORGE_RUNTIMES, null);
		verifyDefaultRuntime(forgeRuntimesPrefs);
		verifyRuntimes(forgeRuntimesPrefs);
	}
	
	private void verifyDefaultRuntime(String xml) {
		String searchString = "default=\"";
		int start = xml.indexOf(searchString);
		assertTrue(start != -1);
		start = start + searchString.length();
		assertTrue(start < xml.length());
		int end = xml.indexOf("\">", start);
		assertTrue(end != -1);
		assertEquals("foo", xml.substring(start, end));		
	}
	
	private void verifyRuntimes(String xml) {
		int startIndex = 0;
		for (int i = 0; i < 3; i++) {
			int start = xml.indexOf("<runtime ", startIndex);
			assertTrue(start != -1);
			int end = xml.indexOf("/>", start);
			assertTrue(end != -1);
			verifyRuntime(xml.substring(start, end + 2));
			startIndex = end + 2;
		}
	}
	
	private void verifyRuntime(String xml) {
		int start = xml.indexOf("name=\"");
		assertTrue(start != -1);
		int end = xml.indexOf("\"", start);
		assertTrue(end != -1);
		String name = xml.substring(start, end);
		if ("embedded".equals(name)) {
			verifyEmbedded(xml);
		} else if ("foo".equals(name)) {
			verifyFoo(xml);
		} else if ("bar".equals(name)) {
			verifyBar(xml);
		}
	}
	
	private void verifyEmbedded(String xml) {
		verifyAttribute("name", "embedded", xml);
		verifyAttribute("type", "embedded", xml);
		verifyAttribute("location", null, xml);
	}
	
	private void verifyFoo(String xml) {
		verifyAttribute("name", "foo", xml);
		verifyAttribute("type", "external", xml);
		verifyAttribute("location", "foofoo", xml);
	}
	
	private void verifyBar(String xml) {
		verifyAttribute("name", "bar", xml);
		verifyAttribute("type", "external", xml);
		verifyAttribute("location", "barbar", xml);
	}
	
	private void verifyAttribute(String name, String expectedValue, String string) {
		int start = string.indexOf(name + "=\"");
		if (start == -1) {
			assertNull(expectedValue);
		} else {
			start += name.length() + 2;
			int end = string.indexOf('\"', start);
			assertTrue(end != -1);
			assertEquals(expectedValue, string.substring(start, end));
		}
	}
	
	@Test
	public void testGetDefaultAndGetInstallationsAlternativeCase() {
		// trick the preferences into initializing from the alternative preference string
		ForgeRuntimesPreferences.INSTANCE.setRuntimes(new ForgeRuntime[0], null);
		InstanceScope.INSTANCE.getNode(ForgeCorePlugin.PLUGIN_ID).put(
				ForgeRuntimesPreferences.PREF_FORGE_RUNTIMES, 
				ALTERNATIVE_FORGE_RUNTIMES);
		// getDefault() will now trigger the initialization
		ForgeRuntime runtime = ForgeRuntimesPreferences.INSTANCE.getDefault();
		assertEquals("foo", runtime.getName());
		assertEquals("foofoo", runtime.getLocation());
		assertEquals("external", runtime.getType());
		// getRuntimes() will return the list of runtimes from the alternative preference string
		ForgeRuntime[] runtimes = ForgeRuntimesPreferences.INSTANCE.getRuntimes();
		verifyEmbedded(runtimes);
		verifyFoo(runtimes);
		verifyBar(runtimes);
	}
	
	private ForgeRuntime getRuntimeToVerify(String name, ForgeRuntime[] runtimes) {
		ForgeRuntime result = null;
		for (int i = 0; i < runtimes.length; i++) {
			if (name.equals(runtimes[i].getName())) {
				result = runtimes[i];
			}
		}
		return result;
	}
	
	private void verifyEmbedded(ForgeRuntime[] runtimes) {
		assertEquals(ForgeEmbeddedRuntime.INSTANCE, getRuntimeToVerify("embedded", runtimes));
	}
	
	private void verifyFoo(ForgeRuntime[] runtimes) {
		ForgeRuntime runtimeToVerify = getRuntimeToVerify("foo", runtimes);
		assertNotNull(runtimeToVerify);
		assertEquals("foo", runtimeToVerify.getName());
		assertEquals("foofoo", runtimeToVerify.getLocation());
		assertEquals("external", runtimeToVerify.getType());
	}
	
	private void verifyBar(ForgeRuntime[] runtimes) {
		ForgeRuntime runtimeToVerify = getRuntimeToVerify("bar", runtimes);
		assertNotNull(runtimeToVerify);
		assertEquals("bar", runtimeToVerify.getName());
		assertEquals("barbar", runtimeToVerify.getLocation());
		assertEquals("external", runtimeToVerify.getType());
	}
	
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
