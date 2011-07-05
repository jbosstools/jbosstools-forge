package org.jboss.tools.forge.core.process;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ForgeExternalRuntimeTest {

	@Test
	public void test() {
		ForgeExternalRuntime runtime = new ForgeExternalRuntime("foo", "bar");
		assertEquals("foo", runtime.getName());
		assertEquals("bar", runtime.getLocation());
		assertEquals("external", runtime.getType());
	}

}
