package org.jboss.tools.aesh.core.internal.ansi;

import org.junit.Assert;
import org.junit.Test;

public class CursorDownTest {
	
	@Test
	public void testGetType() {
		CursorDown cursorDown = new CursorDown(null);
		Assert.assertEquals(CommandType.CURSOR_DOWN, cursorDown.getType());
	}

}
