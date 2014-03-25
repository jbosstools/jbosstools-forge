package org.jboss.tools.aesh.ui.internal;

import org.junit.Assert;
import org.junit.Test;

public class AeshUIPluginTest {

	@Test
	public void testGetDefault() {
		Assert.assertNotNull(AeshUIPlugin.getDefault());
	}

}
