/**
 * Copyright (c) Red Hat, Inc., contributors and others 2004 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.forge.core.process;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.model.IProcess;
import org.jboss.tools.forge.core.internal.process.ForgeLaunchHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
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

	@Ignore
	@Test
	public void test() {
		forgeProcess = ForgeLaunchHelper.launch("test", testLocation);
		assertNotNull(forgeProcess);
		assertFalse(forgeProcess.isTerminated());
	}

}
