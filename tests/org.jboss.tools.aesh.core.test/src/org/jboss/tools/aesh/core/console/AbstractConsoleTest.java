/**
 * Copyright (c) Red Hat, Inc., contributors and others 2004 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.aesh.core.console;

import java.io.InputStream;
import java.io.OutputStream;

import org.jboss.tools.aesh.core.document.Document;
import org.jboss.tools.aesh.core.internal.io.AeshInputStream;
import org.jboss.tools.aesh.core.test.util.TestDocument;
import org.junit.Assert;
import org.junit.Test;

public class AbstractConsoleTest {
	
	private final static byte[] OUT_BUFFER = new byte[] { 'o', 'u', 't' };
	private final static byte[] ERROR_BUFFER = new byte[] { 'e', 'r', 'r', 'o', 'r' };

	private String replacedString = null;
	
	private AbstractConsole console = new AbstractConsole() {
		@Override public void start() {}
		@Override public void stop() {}
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
		Assert.assertNull(console.getInputStream());
		Assert.assertNotEquals("test", new String(buffer));
		AeshInputStream inputStream = new AeshInputStream();
		console.setInputStream(inputStream);
		console.sendInput("test");
		inputStream.read(buffer);
		Assert.assertEquals("test", new String(buffer));
	}
	
	@Test
	public void testConnect() throws Exception {
		Assert.assertNull(replacedString);
		Assert.assertNull(console.getInputStream());
		Assert.assertNull(console.getOutputStream());
		Assert.assertNull(console.getErrorStream());
		console.connect(testDocument);
		InputStream inputStream = console.getInputStream();
		OutputStream outputStream = console.getOutputStream();
		OutputStream errorStream = console.getErrorStream();
		Assert.assertNotNull(inputStream);
		Assert.assertNotNull(outputStream);
		Assert.assertNotNull(errorStream);
		outputStream.write(OUT_BUFFER);
		Assert.assertEquals("out", replacedString);
		errorStream.write(ERROR_BUFFER);
		Assert.assertEquals("error", replacedString);
	}
	
	@Test
	public void testDisconnect() throws Exception {
		console.connect(testDocument);
		OutputStream outputStream = console.getOutputStream();
		OutputStream errorStream = console.getErrorStream();
		outputStream.write(OUT_BUFFER);
		Assert.assertEquals("out", replacedString);
		errorStream.write(ERROR_BUFFER);
		Assert.assertEquals("error", replacedString);
		replacedString = null;
		console.disconnect();
		outputStream.write(OUT_BUFFER);
		Assert.assertNull(replacedString);
		errorStream.write(ERROR_BUFFER);
		Assert.assertNull(replacedString);
		Assert.assertNull(console.getInputStream());
		Assert.assertNull(console.getOutputStream());
		Assert.assertNull(console.getErrorStream());
	}

}
