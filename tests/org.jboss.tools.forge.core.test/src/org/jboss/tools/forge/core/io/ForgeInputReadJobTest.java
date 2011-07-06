package org.jboss.tools.forge.core.io;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.debug.core.model.IStreamMonitor;
import org.eclipse.debug.core.model.IStreamsProxy;
import org.junit.Before;
import org.junit.Test;

public class ForgeInputReadJobTest {
	
	private int[] in;
	private String out;
	
	@Before
	public void setUp() {
		in = null;
		out = "";
	}

	@Test
	public void testNullInput() {
		ForgeInputReadJob forgeInputReadJob = new ForgeInputReadJob(new TestStreamProxy(), null);
		forgeInputReadJob.schedule();
		try {
			forgeInputReadJob.join();
		} catch (InterruptedException e) {}
		assertEquals("", out);
	}
	
	@Test
	public void testNormalInput() {
		in = new int[] { 'b', 'l', 'a', 'h', -1 }; 
		ForgeInputReadJob forgeInputReadJob = new ForgeInputReadJob(new TestStreamProxy(), new TestInputStream());
		forgeInputReadJob.schedule();
		try {
			forgeInputReadJob.join();
		} catch (InterruptedException e) {}
		assertEquals("blah", out);
	}
	
	private class TestStreamProxy implements IStreamsProxy {
		public IStreamMonitor getErrorStreamMonitor() {
			return null;
		}
		public IStreamMonitor getOutputStreamMonitor() {
			return null;
		}
		public void write(String str) throws IOException {
			out += str;
		}		
	}
	
	private class TestInputStream extends InputStream {
		int index = 0;
		public int read() throws IOException {
			int result = in.length > index ? in[index] : -1;
			index++;
			return result;
		}		
	}

}
