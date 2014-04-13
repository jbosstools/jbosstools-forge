package org.jboss.tools.aesh.ui.internal.document;


import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.widgets.Display;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class StyleImplTest {
	
	private StyleRange testStyleRange;
	private StyleImpl testStyleImpl;
	
	@Before
	public void setup() {
		testStyleRange = 
				new StyleRange(
						99, 
						999, 
						Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY), 
						Display.getDefault().getSystemColor(SWT.COLOR_GRAY));
		testStyleImpl = new StyleImpl(testStyleRange);
	}
	
	@Test
	public void testConstructor() {
		StyleRange styleRange = new StyleRange();
		StyleImpl styleImpl = new StyleImpl(styleRange);
		Assert.assertEquals(styleRange, styleImpl.styleRange);
	}
	
	@Test
	public void testGetStyleRange() {
		Assert.assertEquals(testStyleRange, testStyleImpl.getStyleRange());
	}
	

}
