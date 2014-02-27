package org.jboss.tools.aesh.core.internal.io;

import org.junit.Assert;
import org.junit.Test;

public class AeshInputStreamTest {
	
	@Test
	public void testAppend() throws Exception {
		AeshInputStream ais = new AeshInputStream();
		byte[] bytes = new byte[4];
		ais.append("test");
		ais.read(bytes);
		Assert.assertEquals("test", new String(bytes));
		ais.close();
	}

}
