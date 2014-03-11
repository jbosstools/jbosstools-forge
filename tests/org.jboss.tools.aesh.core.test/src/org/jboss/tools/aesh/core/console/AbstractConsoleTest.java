package org.jboss.tools.aesh.core.console;

import java.io.InputStream;
import java.io.OutputStream;

import org.jboss.tools.aesh.core.document.Document;
import org.jboss.tools.aesh.core.test.util.TestDocument;
import org.junit.Assert;
import org.junit.Test;

public class AbstractConsoleTest {
	
	private String replacedString = null;
	
	private AbstractConsole console = new AbstractConsole() {
		@Override public void start() {}
		@Override public void stop() {}
		@Override protected void createConsole() {}		
	};
	
	private Document testDocument = new TestDocument() {
		@Override 
		public void replace(int offset, int length, String string) {
			replacedString = string;
		}
	};
	
	@Test
	public void testSendInput() throws Exception {
		byte[] buffer = new byte[4];
		InputStream inputStream = console.getInputStream();
		Assert.assertNotNull(inputStream);
		Assert.assertNotEquals("test", new String(buffer));
		console.sendInput("test");
		inputStream.read(buffer);
		Assert.assertEquals("test", new String(buffer));
	}
	
	@Test
	public void testConnectAndDisconnect() throws Exception {
		byte[] outBuffer = new byte[] { 'o', 'u', 't' };
		byte[] errorBuffer = new byte[] { 'e', 'r', 'r', 'o', 'r' };
		Assert.assertNull(replacedString);
		OutputStream outputStream = console.getOutputStream();
		Assert.assertNotNull(outputStream);
		OutputStream errorStream = console.getErrorStream();
		Assert.assertNotNull(errorStream);
		outputStream.write(outBuffer);
		Assert.assertNull(replacedString);
		errorStream.write(errorBuffer);
		Assert.assertNull(replacedString);
		console.connect(testDocument);
		outputStream.write(outBuffer);
		Assert.assertEquals("out", replacedString);
		errorStream.write(errorBuffer);
		Assert.assertEquals("error", replacedString);
		replacedString = null;
		console.disconnect();
		outputStream.write(outBuffer);
		Assert.assertNull(replacedString);
		errorStream.write(errorBuffer);
		Assert.assertNull(replacedString);
	}

}
