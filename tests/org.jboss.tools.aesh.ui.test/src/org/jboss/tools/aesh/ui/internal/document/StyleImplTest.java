package org.jboss.tools.aesh.ui.internal.document;


import org.eclipse.swt.custom.StyleRange;
import org.jboss.tools.aesh.ui.internal.util.ColorConstants;
import org.jboss.tools.aesh.ui.internal.util.FontManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class StyleImplTest {
	
	private StyleRange testStyleRange;
	private StyleImpl testStyleImpl;
	
	@Before
	public void setup() {
		testStyleRange = new StyleRange();
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
	
	@Test
	public void testResetToNormal() {
		testStyleRange.font = FontManager.INSTANCE.getItalicBold();
		testStyleRange.background = ColorConstants.CYAN;
		testStyleRange.foreground = ColorConstants.GREEN;
		testStyleImpl.resetToNormal();
		Assert.assertEquals(FontManager.INSTANCE.getDefault(), testStyleRange.font);
		Assert.assertEquals(ColorConstants.DEFAULT_BACKGROUND, testStyleRange.background);
		Assert.assertEquals(ColorConstants.DEFAULT_FOREGROUND, testStyleRange.foreground);
	}
	

}
