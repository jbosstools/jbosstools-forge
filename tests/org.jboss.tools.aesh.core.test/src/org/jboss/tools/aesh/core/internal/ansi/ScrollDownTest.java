package org.jboss.tools.aesh.core.internal.ansi;

import org.junit.Assert;
import org.junit.Test;

public class ScrollDownTest {
	
	@Test
	public void testGetType() {
		ScrollDown scrollDown = new ScrollDown(null);
		Assert.assertEquals(CommandType.SCROLL_DOWN, scrollDown.getType());
	}

}
