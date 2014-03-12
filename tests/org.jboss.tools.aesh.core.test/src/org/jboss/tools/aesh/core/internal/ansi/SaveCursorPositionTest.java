package org.jboss.tools.aesh.core.internal.ansi;

import org.jboss.tools.aesh.core.document.Document;
import org.jboss.tools.aesh.core.test.util.TestDocument;
import org.junit.Assert;
import org.junit.Test;

public class SaveCursorPositionTest {

	private boolean cursorSaved = false;
	
	private Document testDocument = new TestDocument() {
		@Override public void saveCursor() { cursorSaved = true; }
	};
	
	@Test
	public void testGetType() {
		SaveCursorPosition saveCursorPosition = new SaveCursorPosition(null);
		Assert.assertEquals(
				CommandType.SAVE_CURSOR_POSITION, 
				saveCursorPosition.getType());
	}
	
	@Test
	public void testHandle() {
		SaveCursorPosition saveCursorPosition = new SaveCursorPosition(null);
		Assert.assertFalse(cursorSaved);
		saveCursorPosition.handle(testDocument);
		Assert.assertTrue(cursorSaved);
	}


}
