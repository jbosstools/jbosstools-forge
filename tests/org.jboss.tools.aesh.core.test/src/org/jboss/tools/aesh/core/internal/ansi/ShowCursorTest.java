package org.jboss.tools.aesh.core.internal.ansi;

import org.junit.Assert;
import org.junit.Test;

public class ShowCursorTest {

	@Test
	public void testGetType() {
		ShowCursor showCursor = new ShowCursor(null);
		Assert.assertEquals(CommandType.SHOW_CURSOR, showCursor.getType());
	}

}
