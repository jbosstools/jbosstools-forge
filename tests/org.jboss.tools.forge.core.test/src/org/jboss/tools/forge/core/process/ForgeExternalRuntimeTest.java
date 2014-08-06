/**
 * Copyright (c) Red Hat, Inc., contributors and others 2004 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.forge.core.process;

import static org.junit.Assert.assertEquals;

import org.jboss.tools.forge.core.internal.runtime.ForgeExternalRuntime;
import org.jboss.tools.forge.core.runtime.ForgeRuntimeType;
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
		assertEquals(ForgeRuntimeType.EXTERNAL, runtime.getType());
	}
	
	@Test
	public void testSetters() {
		runtime.setName("Honolulu");
		assertEquals("Honolulu", runtime.getName());
		runtime.setLocation("Hawaii");
		assertEquals("Hawaii", runtime.getLocation());
	}

}
