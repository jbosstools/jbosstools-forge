package org.jboss.tools.forge.core.process;

import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IProcess;

import junit.framework.TestCase;

public class ForgeLaunchHelperTest extends TestCase {
	
	private static final String PREFIX = "reference:file:";
	private IProcess forgeProcess = null;
	
	protected void setUp() {
		forgeProcess = null;
	}
	
	public void testLaunch() {
		try {
			String location = Platform.getBundle("org.jboss.tools.forge.runtime").getLocation();
			assertTrue(location.startsWith(PREFIX));
			location = location.substring(PREFIX.length());
			forgeProcess = ForgeLaunchHelper.launch("test", location);
			assertNotNull(forgeProcess);
			assertFalse(forgeProcess.isTerminated());
		} catch (Throwable t) {
			t.printStackTrace();
			fail();
		}
	}
	
	protected void tearDown() {
		if (forgeProcess != null) {
			try {
				forgeProcess.terminate();
			} catch (DebugException e) {}
		}
		forgeProcess = null;
	}

}
