package org.jboss.tools.aesh.core.internal.ansi;

import org.junit.Assert;
import org.junit.Test;

public class HorizontalAndVerticalPositionTest {
	
	@Test
	public void testGetType() {
		HorizontalAndVerticalPosition horizontalAndVerticalPosition = 
				new HorizontalAndVerticalPosition(null);
		Assert.assertEquals(
				CommandType.HORIZONTAL_AND_VERTICAL_POSITION, 
				horizontalAndVerticalPosition.getType());
	}

}
