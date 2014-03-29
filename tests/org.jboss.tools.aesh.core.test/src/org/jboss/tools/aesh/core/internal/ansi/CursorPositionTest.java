package org.jboss.tools.aesh.core.internal.ansi;

import org.jboss.tools.aesh.core.document.Document;
import org.jboss.tools.aesh.core.test.util.TestDocument;
import org.junit.Assert;
import org.junit.Test;

public class CursorPositionTest {
	
	private int testOffset = 0;
	
	private Document testDocument = new TestDocument() {
		@Override public int getLineOffset(int line) { return line * 80; }
		@Override public int getLineLength(int line) { return 55; }
		@Override public void moveCursorTo(int offset) { testOffset = offset; }
	};
	
	@Test
	public void testGetType() {
		CursorPosition cursorPosition = new CursorPosition("4;5");
		Assert.assertEquals(CommandType.CURSOR_POSITION, cursorPosition.getType());
	}
	
	@Test
	public void testHandle() {
		CursorPosition cursorPosition = new CursorPosition("4");
		cursorPosition.handle(testDocument);
		Assert.assertEquals(testOffset, 240);
		cursorPosition = new CursorPosition("5;100");
		cursorPosition.handle(testDocument);
		Assert.assertEquals(testOffset, 375);
		cursorPosition = new CursorPosition("3;45");
		cursorPosition.handle(testDocument);
		Assert.assertEquals(testOffset, 204);
	}

}
