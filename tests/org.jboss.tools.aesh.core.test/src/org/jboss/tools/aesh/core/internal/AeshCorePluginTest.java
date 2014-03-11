package org.jboss.tools.aesh.core.internal;

import org.junit.Assert;
import org.junit.Test;

public class AeshCorePluginTest {
	
	@Test
	public void testGetDefault() {
		Assert.assertNotNull(AeshCorePlugin.getDefault());
	}

}
