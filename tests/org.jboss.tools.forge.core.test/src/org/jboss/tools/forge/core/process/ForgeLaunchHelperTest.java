package org.jboss.tools.forge.core.process;

import static org.junit.Assert.fail;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;

import java.io.IOException;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.ui.progress.UIJob;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ForgeLaunchHelperTest {

	private static String TEST_LOCATION;
	
	private IProcess forgeProcess = null;
	
	static {
		try {
			TEST_LOCATION = FileLocator.getBundleFile(Platform.getBundle("org.jboss.tools.forge.runtime")).getAbsolutePath();
		} catch (IOException e) {
			TEST_LOCATION = null;
		}
	}

	@Before
	public void setUp() throws Exception {
		forgeProcess = null;
	}

	@After
	public void tearDown() throws Exception {
		if (forgeProcess != null) {
			try {
				forgeProcess.terminate();
			} catch (DebugException e) {}
		}
		forgeProcess = null;
	}

	@Test
	public void test() {
		new UIJob("testLaunch") {
			public IStatus runInUIThread(IProgressMonitor monitor) {
				try {
					forgeProcess = ForgeLaunchHelper.launch("test", TEST_LOCATION);
					assertNotNull(forgeProcess);
					assertFalse(forgeProcess.isTerminated());
				} catch (RuntimeException e) {
					fail();
				}
				return null;
			}
			
		}.schedule();
	}

}
