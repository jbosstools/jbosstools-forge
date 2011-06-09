package org.jboss.tools.forge.core.process;

import junit.framework.TestCase;

import org.eclipse.core.runtime.Platform;

public class ForgeRuntimeTest extends TestCase {
	
	private ForgeRuntime forgeRuntime;
	
	protected void setUp() {
		String location = Platform.getBundle("org.jboss.tools.forge.runtime").getLocation();
		location = location.substring("reference:file:".length());
		forgeRuntime = new ForgeRuntime("forge-test", location);
	}
	
	public void testStartStop() {
		assertTrue(forgeRuntime.isTerminated());
		forgeRuntime.start();
		assertFalse(forgeRuntime.isTerminated());
		forgeRuntime.stop();
		assertTrue(forgeRuntime.isTerminated());
	}
	
	protected void tearDown() {
		forgeRuntime = null;
	}

}
