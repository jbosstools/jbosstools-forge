package org.jboss.tools.forge.core.process;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.jboss.tools.forge.core.internal.runtime.ForgeEmbeddedRuntime;
import org.jboss.tools.forge.core.runtime.ForgeRuntimeType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ForgeEmbeddedRuntimeTest {
	
	private String location = null;
	private String name = null;
	
	@Before 
	public void setUp() throws Exception {
		File file = FileLocator.getBundleFile(Platform.getBundle("org.jboss.tools.forge.runtime"));
		for (String str : file.list()) {
			if (str.startsWith("forge-distribution-")) {
				location = file.getAbsolutePath() + File.separator + str;
			}
		}
		name = "1.4.4.Final - embedded";
	}
	
	@After
	public void tearDown() {
		location = null;
	}
	

	@Test
	public void testForgeEmbeddedRuntime() {
		assertNotNull(ForgeEmbeddedRuntime.INSTANCE);
		assertEquals(name, ForgeEmbeddedRuntime.INSTANCE.getName());
		assertEquals(location, ForgeEmbeddedRuntime.INSTANCE.getLocation());
		assertEquals(ForgeRuntimeType.EMBEDDED, ForgeEmbeddedRuntime.INSTANCE.getType());
	}

}
