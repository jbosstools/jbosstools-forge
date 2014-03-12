package org.jboss.tools.aesh.core.internal.ansi;

import org.junit.Assert;
import org.junit.Test;

public class HideCursorTest {
	
	@Test
	public void testGetType() {
		HideCursor hideCursor = new HideCursor(null);
		Assert.assertEquals(CommandType.HIDE_CURSOR, hideCursor.getType());
	}

}
