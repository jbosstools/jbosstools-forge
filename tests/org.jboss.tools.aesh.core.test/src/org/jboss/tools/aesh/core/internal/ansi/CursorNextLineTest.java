package org.jboss.tools.aesh.core.internal.ansi;

import org.junit.Assert;
import org.junit.Test;

public class CursorNextLineTest {
	
	@Test
	public void testGetType() {
		CursorNextLine cursorNextLine = new CursorNextLine(null);
		Assert.assertEquals(CommandType.CURSOR_NEXT_LINE, cursorNextLine.getType());
	}

}
