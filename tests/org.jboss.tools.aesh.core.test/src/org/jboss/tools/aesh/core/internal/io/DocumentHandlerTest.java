package org.jboss.tools.aesh.core.internal.io;

import org.jboss.tools.aesh.core.document.Document;
import org.jboss.tools.aesh.core.document.Style;
import org.jboss.tools.aesh.core.internal.ansi.Command;
import org.jboss.tools.aesh.core.test.util.TestDocument;
import org.jboss.tools.aesh.core.test.util.TestStyle;
import org.junit.Assert;
import org.junit.Test;

public class DocumentHandlerTest {
	
	private int styleLength = 0;
	private int documentLength = 0;
	private int cursorOffset = 0;
	
	private int replacedOffset = 0;
	private int replacedLength = 0;
	private String replacedText = null;
	private Document handledDocument = null;
	
	private Style testStyle = new TestStyle() {
		@Override public int getLength() { return styleLength; }
		@Override public void setLength(int length) { styleLength = length; }
	};
	
	private Document testDocument = new TestDocument() {
		@Override public Style getCurrentStyle() { return testStyle; }
		@Override public int getCursorOffset() { return cursorOffset; }
		@Override public int getLength() { return documentLength; }
		@Override public void moveCursorTo(int offset) { cursorOffset = offset; }
		@Override public void replace(int offset, int length, String text) { 
			replacedOffset = offset;
			replacedLength = length;
			replacedText = text;
		}
	};
	
	private Command testCommand = new Command() {		
		@Override
		public void handle(Document document) {
			handledDocument = document;
		}
	};
	
	private DocumentHandler testHandler = new DocumentHandler();
	
	private String testOutput = "foobar";
	
	@Test
	public void testHandleOutput() {
		testHandler.setDocument(testDocument);
		testHandler.handleOutput(testOutput);
		Assert.assertEquals("style length", testOutput.length(), styleLength);
		Assert.assertEquals("replaced offset", 0, replacedOffset);
		Assert.assertEquals("replaced lenght", 0, replacedLength);
		Assert.assertEquals("replaced text", testOutput, replacedText);
		Assert.assertEquals("cursor offset", testOutput.length(), cursorOffset);
	}
	
	@Test
	public void testHandleCommand() {
		testHandler.setDocument(testDocument);
		testHandler.handleCommand(testCommand);
		Assert.assertEquals("handled document", testDocument, handledDocument);
	}

}
