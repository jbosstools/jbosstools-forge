package org.jboss.tools.aesh.core.internal.ansi;

import org.junit.Assert;
import org.junit.Test;

public class CursorUpTest {
	
	@Test
	public void testGetType() {
		CursorUp cursorUp = new CursorUp(null);
		Assert.assertEquals(CommandType.CURSOR_UP, cursorUp.getType());
	}

}
