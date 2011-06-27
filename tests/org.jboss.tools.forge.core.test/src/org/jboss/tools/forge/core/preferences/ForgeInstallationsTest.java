package org.jboss.tools.forge.core.preferences;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.jboss.tools.forge.core.process.ForgeRuntime;
import org.junit.Test;

public class ForgeInstallationsTest {
	
	private static String defaultForgeLocation = null;
	
	static {
		try {
			defaultForgeLocation = FileLocator.getBundleFile(Platform.getBundle("org.jboss.tools.forge.runtime")).getAbsolutePath();
		} catch (IOException e) {
			// ignore
		}
		
	}
	
	@Test
	public void testGetDefault() {
		ForgeRuntime forgeRuntime = ForgeInstallations.getDefault();
		assertNotNull(forgeRuntime);
		assertEquals("embedded", forgeRuntime.getName());
		assertEquals(defaultForgeLocation, forgeRuntime.getLocation());
	}

}
