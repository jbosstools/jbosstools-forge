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
	
	@Test
	public void testSetBoldOn() {
		testStyleRange.font = FontManager.INSTANCE.getDefault();
		testStyleImpl.setBoldOn();
		Assert.assertEquals(FontManager.INSTANCE.getBold(), testStyleRange.font);
		testStyleRange.font = FontManager.INSTANCE.getItalicBold();
		testStyleImpl.setBoldOn();
		Assert.assertEquals(FontManager.INSTANCE.getItalicBold(), testStyleRange.font);
		testStyleRange.font = FontManager.INSTANCE.getItalic();
		testStyleImpl.setBoldOn();
		Assert.assertEquals(FontManager.INSTANCE.getItalicBold(), testStyleRange.font);
		testStyleRange.font = FontManager.INSTANCE.getBold();
		testStyleImpl.setBoldOn();
		Assert.assertEquals(FontManager.INSTANCE.getBold(), testStyleRange.font);
	}
	
	@Test
	public void testSetFaintOn() {
		// setFaintOn() is ignored for now
		Assert.assertTrue(true);
	}
	
	@Test
	public void setItalicOn() {
		testStyleRange.font = FontManager.INSTANCE.getDefault();
		testStyleImpl.setItalicOn();
		Assert.assertEquals(FontManager.INSTANCE.getItalic(), testStyleRange.font);
		testStyleRange.font = FontManager.INSTANCE.getItalicBold();
		testStyleImpl.setItalicOn();
		Assert.assertEquals(FontManager.INSTANCE.getItalicBold(), testStyleRange.font);
		testStyleRange.font = FontManager.INSTANCE.getBold();
		testStyleImpl.setItalicOn();
		Assert.assertEquals(FontManager.INSTANCE.getItalicBold(), testStyleRange.font);
		testStyleRange.font = FontManager.INSTANCE.getItalic();
		testStyleImpl.setItalicOn();
		Assert.assertEquals(FontManager.INSTANCE.getItalic(), testStyleRange.font);
	}

}
