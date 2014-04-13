package org.jboss.tools.aesh.ui.internal.document;


import org.eclipse.swt.custom.StyleRange;
import org.junit.Assert;
import org.junit.Test;

public class StyleImplTest {
	
	@Test
	public void testConstructor() {
		StyleRange styleRange = new StyleRange();
		StyleImpl styleImpl = new StyleImpl(styleRange);
		Assert.assertEquals(styleRange, styleImpl.styleRange);
	}

}
