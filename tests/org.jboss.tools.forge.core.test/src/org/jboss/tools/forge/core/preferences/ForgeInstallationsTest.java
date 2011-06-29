package org.jboss.tools.forge.core.preferences;

import static org.junit.Assert.assertEquals;

import org.jboss.tools.forge.core.process.ForgeEmbeddedRuntime;
import org.junit.Test;

public class ForgeInstallationsTest {
	
	@Test
	public void testGetDefault() {
		assertEquals(ForgeEmbeddedRuntime.INSTANCE, ForgeInstallations.getDefault());
	}
	
}
