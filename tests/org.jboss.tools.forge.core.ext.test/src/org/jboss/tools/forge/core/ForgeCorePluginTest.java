package org.jboss.tools.forge.core;

import static org.junit.Assert.*;

import org.junit.Test;

public class ForgeCorePluginTest {

	@Test
	public void test() {
		assertNotNull(ForgeCorePlugin.getDefault());
	}

}
