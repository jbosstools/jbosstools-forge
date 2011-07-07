package org.jboss.tools.forge.core.process;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class ForgeExternalRuntimeTest {
	
	private ForgeExternalRuntime runtime;
	
	@Before
	public void setUp() {
		runtime = new ForgeExternalRuntime("foo", "bar");
	}

	@Test
	public void testGetters() {
		assertEquals("foo", runtime.getName());
		assertEquals("bar", runtime.getLocation());
		assertEquals("external", runtime.getType());
	}
	
	@Test
	public void testSetters() {
		runtime.setName("Honolulu");
		assertEquals("Honolulu", runtime.getName());
		runtime.setLocation("Hawaii");
		assertEquals("Hawaii", runtime.getLocation());
	}

}
