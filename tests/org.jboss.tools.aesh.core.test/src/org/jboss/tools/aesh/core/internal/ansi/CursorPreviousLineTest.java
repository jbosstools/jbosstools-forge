package org.jboss.tools.aesh.core.internal.ansi;

import org.junit.Assert;
import org.junit.Test;

public class CursorPreviousLineTest {
	
	@Test
	public void testGetType() {
		CursorPreviousLine cursorPreviousLine = new CursorPreviousLine(null);
		Assert.assertEquals(CommandType.CURSOR_PREVIOUS_LINE, cursorPreviousLine.getType());
	}

}
