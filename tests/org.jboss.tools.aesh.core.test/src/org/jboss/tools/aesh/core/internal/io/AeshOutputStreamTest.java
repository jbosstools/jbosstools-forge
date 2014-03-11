package org.jboss.tools.aesh.core.internal.io;

import org.junit.Assert;
import org.junit.Test;

public class AeshOutputStreamTest {
	
	private String filteredOutput = null;
	
	private AeshOutputFilter testFilter = new AeshOutputFilter() {		
		@Override
		public void filterOutput(String output) {
			filteredOutput = output;
		}
	};
	
	private AeshOutputStream aeshOutputStream = null;
	
	@Test
	public void testWrite() throws Exception {
		Assert.assertNull(filteredOutput);
		aeshOutputStream = new AeshOutputStream(testFilter);
		aeshOutputStream.write(65);
		Assert.assertEquals("A", filteredOutput);
		aeshOutputStream.write(new byte[] { 't', 'e', 's', 't' });
		Assert.assertEquals("test", filteredOutput);
	}

}
