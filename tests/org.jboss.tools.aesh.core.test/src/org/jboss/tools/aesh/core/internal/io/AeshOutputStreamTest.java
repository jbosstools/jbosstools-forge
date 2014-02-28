package org.jboss.tools.aesh.core.internal.io;

import org.jboss.tools.aesh.core.io.StreamListener;
import org.junit.Assert;
import org.junit.Test;

public class AeshOutputStreamTest {
	
	private StreamListener streamListener = new StreamListener() {		
		@Override
		public void outputAvailable(String str) {
			availableOutput = str;
		}
	};
	
	private String availableOutput = null;
	
	@Test
	public void testAeshOutputStream() throws Exception {
		Assert.assertNotNull(AeshOutputStream.STD_OUT);
		Assert.assertNotNull(AeshOutputStream.STD_ERR);
		AeshOutputStream.STD_OUT.addStreamListener(streamListener);
		AeshOutputStream.STD_OUT.write((int)'a');
		Assert.assertEquals("a", availableOutput);
		AeshOutputStream.STD_OUT.write("foo".getBytes(), 0, 3);
		Assert.assertEquals("foo", availableOutput);
		AeshOutputStream.STD_OUT.removeStreamListener(streamListener);
		AeshOutputStream.STD_OUT.write("bar".getBytes(), 0, 3);
		Assert.assertNotEquals("bar", availableOutput);
	}

}
