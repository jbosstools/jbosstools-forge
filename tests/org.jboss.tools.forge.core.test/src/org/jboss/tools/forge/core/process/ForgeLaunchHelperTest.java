package org.jboss.tools.forge.core.process;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.model.IProcess;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ForgeLaunchHelperTest {

	private String testLocation = null;	
	private IProcess forgeProcess = null;
	
	@Before
	public void setUp() throws Exception {
		testLocation = FileLocator.getBundleFile(Platform.getBundle("org.jboss.tools.forge.runtime")).getAbsolutePath();
	}

	@After
	public void tearDown() throws Exception {
		forgeProcess.terminate();
		forgeProcess = null;
		testLocation = null;
	}

	@Test
	public void test() {
		forgeProcess = ForgeLaunchHelper.launch("test", testLocation);
		assertNotNull(forgeProcess);
		assertFalse(forgeProcess.isTerminated());
	}

}
