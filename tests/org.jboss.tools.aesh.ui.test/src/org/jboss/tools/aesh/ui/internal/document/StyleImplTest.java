package org.jboss.tools.aesh.ui.internal.document;


import org.eclipse.swt.SWT;
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
	public void testSetItalicOn() {
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
	
	@Test
	public void testSetUnderlineSingle() {
		Assert.assertFalse(testStyleRange.underline);
		Assert.assertEquals(SWT.NONE, testStyleRange.underlineStyle);
		testStyleImpl.setUnderlineSingle();
		Assert.assertTrue(testStyleRange.underline);
		Assert.assertEquals(SWT.SINGLE, testStyleRange.underlineStyle);
	}
	
	@Test
	public void testSetImageNegative() {
		testStyleRange.background = ColorConstants.CYAN;
		testStyleRange.foreground = ColorConstants.GREEN;
		Assert.assertFalse(testStyleImpl.imageNegative);
		testStyleImpl.setImageNegative();
		Assert.assertEquals(ColorConstants.GREEN, testStyleRange.background);
		Assert.assertEquals(ColorConstants.CYAN, testStyleRange.foreground);
		Assert.assertTrue(testStyleImpl.imageNegative);
		testStyleImpl.setImageNegative();
		Assert.assertEquals(ColorConstants.GREEN, testStyleRange.background);
		Assert.assertEquals(ColorConstants.CYAN, testStyleRange.foreground);
		Assert.assertTrue(testStyleImpl.imageNegative);
	}
	
	@Test
	public void testSetCrossedOut() {
		testStyleRange.strikeout = false;
		testStyleImpl.setCrossedOut();
		Assert.assertTrue(testStyleRange.strikeout);
	}
	
	@Test 
	public void testSetBoldOrFaintOff() {
		// only for bold since faint is not implemented
		testStyleRange.font = FontManager.INSTANCE.getDefault();
		testStyleImpl.setBoldOrFaintOff();
		Assert.assertEquals(FontManager.INSTANCE.getDefault(), testStyleRange.font);
		testStyleRange.font = FontManager.INSTANCE.getItalicBold();
		testStyleImpl.setBoldOrFaintOff();
		Assert.assertEquals(FontManager.INSTANCE.getItalic(), testStyleRange.font);
		testStyleRange.font = FontManager.INSTANCE.getBold();
		testStyleImpl.setBoldOrFaintOff();
		Assert.assertEquals(FontManager.INSTANCE.getDefault(), testStyleRange.font);		
		testStyleRange.font = FontManager.INSTANCE.getItalic();
		testStyleImpl.setBoldOrFaintOff();
		Assert.assertEquals(FontManager.INSTANCE.getItalic(), testStyleRange.font);
	}
	
	@Test
	public void testSetItalicOff() {
		testStyleRange.font = FontManager.INSTANCE.getItalic();
		testStyleImpl.setItalicOff();
		Assert.assertEquals(FontManager.INSTANCE.getDefault(), testStyleRange.font);
		testStyleRange.font = FontManager.INSTANCE.getBold();
		testStyleImpl.setItalicOff();
		Assert.assertEquals(FontManager.INSTANCE.getBold(), testStyleRange.font);
		testStyleRange.font = FontManager.INSTANCE.getItalicBold();
		testStyleImpl.setItalicOff();
		Assert.assertEquals(FontManager.INSTANCE.getBold(), testStyleRange.font);		
		testStyleRange.font = FontManager.INSTANCE.getDefault();
		testStyleImpl.setItalicOff();
		Assert.assertEquals(FontManager.INSTANCE.getDefault(), testStyleRange.font);
	}

	@Test
	public void testSetUnderlineNone() {
		testStyleRange.underline = true;
		testStyleRange.underlineStyle = SWT.SINGLE;
		testStyleImpl.setUnderlineNone();
		Assert.assertFalse(testStyleRange.underline);
		Assert.assertEquals(SWT.NONE, testStyleRange.underlineStyle);
	}
	
	@Test
	public void testSetImagePositive() {
		testStyleImpl.imageNegative = true;
		testStyleRange.background = ColorConstants.CYAN;
		testStyleRange.foreground = ColorConstants.GREEN;
		testStyleImpl.setImagePositive();
		Assert.assertEquals(ColorConstants.GREEN, testStyleRange.background);
		Assert.assertEquals(ColorConstants.CYAN, testStyleRange.foreground);
		Assert.assertFalse(testStyleImpl.imageNegative);
		testStyleImpl.setImagePositive();
		Assert.assertEquals(ColorConstants.GREEN, testStyleRange.background);
		Assert.assertEquals(ColorConstants.CYAN, testStyleRange.foreground);
		Assert.assertFalse(testStyleImpl.imageNegative);
	}
	
	@Test
	public void testSetNotCrossedOut() {
		testStyleRange.strikeout = true;
		testStyleImpl.setNotCrossedOut();
		Assert.assertFalse(testStyleRange.strikeout);
	}
	
	@Test
	public void testSetForegroundBlack() {
		testStyleRange.foreground = ColorConstants.BLUE;
		testStyleImpl.setForegroundBlack();
		Assert.assertEquals(ColorConstants.BLACK, testStyleRange.foreground);
	}
	
	@Test
	public void testSetForegroundRed() {
		testStyleRange.foreground = ColorConstants.BLACK;
		testStyleImpl.setForegroundRed();
		Assert.assertEquals(ColorConstants.RED, testStyleRange.foreground);
	}
	
	@Test
	public void testSetForegroundGreen() {
		testStyleRange.foreground = ColorConstants.BLACK;
		testStyleImpl.setForegroundGreen();
		Assert.assertEquals(ColorConstants.GREEN, testStyleRange.foreground);
	}
	
	@Test
	public void testSetForegroundYellow() {
		testStyleRange.foreground = ColorConstants.BLACK;
		testStyleImpl.setForegroundYellow();
		Assert.assertEquals(ColorConstants.YELLOW, testStyleRange.foreground);
	}
	
	@Test
	public void testSetForegroundBlue() {
		testStyleRange.foreground = ColorConstants.BLACK;
		testStyleImpl.setForegroundBlue();
		Assert.assertEquals(ColorConstants.BLUE, testStyleRange.foreground);
	}
	
	@Test
	public void testSetForegroundMagenta() {
		testStyleRange.foreground = ColorConstants.BLACK;
		testStyleImpl.setForegroundMagenta();
		Assert.assertEquals(ColorConstants.MAGENTA, testStyleRange.foreground);
	}
	
	@Test
	public void testSetForegroundCyan() {
		testStyleRange.foreground = ColorConstants.BLACK;
		testStyleImpl.setForegroundCyan();
		Assert.assertEquals(ColorConstants.CYAN, testStyleRange.foreground);
	}
	
	@Test
	public void testSetForegroundWhite() {
		testStyleRange.foreground = ColorConstants.BLACK;
		testStyleImpl.setForegroundWhite();
		Assert.assertEquals(ColorConstants.WHITE, testStyleRange.foreground);
	}
	
	@Test
	public void testSetForegroundXTerm() {
		// not yet implemented
		Assert.assertTrue(true);
	}
	
	@Test
	public void testSetForegroundDefault() {
		testStyleRange.foreground = ColorConstants.DEFAULT_BACKGROUND;
		Assert.assertNotEquals(ColorConstants.DEFAULT_FOREGROUND, testStyleRange.foreground);
		testStyleImpl.setForegroundDefault();
		Assert.assertEquals(ColorConstants.DEFAULT_FOREGROUND, testStyleRange.foreground);
	}
	
}
