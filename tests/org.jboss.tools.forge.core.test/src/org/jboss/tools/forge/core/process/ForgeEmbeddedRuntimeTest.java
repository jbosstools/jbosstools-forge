package org.jboss.tools.forge.core.process;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ForgeEmbeddedRuntimeTest {
	
	private String name = null;
	private String location = null;
	
	@Before 
	public void setUp() throws Exception {
		name = "embedded";
		location = FileLocator.getBundleFile(Platform.getBundle("org.jboss.tools.forge.runtime")).getAbsolutePath();
	}
	
	@After
	public void tearDown() {
		name = null;
		location = null;
	}
	
	@Test
	public void testForgeEmbeddedRuntime() {
		assertNotNull(ForgeEmbeddedRuntime.INSTANCE);
		assertEquals(name, ForgeEmbeddedRuntime.INSTANCE.getName());
		assertEquals(location, ForgeEmbeddedRuntime.INSTANCE.getLocation());
	}

}
