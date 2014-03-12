package org.jboss.tools.aesh.core.internal.ansi;

import org.junit.Assert;
import org.junit.Test;

public class ScrollUpTest {

	@Test
	public void testGetType() {
		ScrollUp scrollUp = new ScrollUp(null);
		Assert.assertEquals(CommandType.SCROLL_UP, scrollUp.getType());
	}

}
