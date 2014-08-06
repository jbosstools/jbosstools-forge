/**
 * Copyright (c) Red Hat, Inc., contributors and others 2004 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.aesh.ui.internal.util;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.junit.Assert;
import org.junit.Test;

public class FontManagerTest {
	
	private Font defaultFont = JFaceResources.getFont(FontManager.AESH_CONSOLE_FONT);
	private String defaultFontName = defaultFont.getFontData()[0].getName();
	private int defaultFontHeight = defaultFont.getFontData()[0].getHeight();
	
	@Test
	public void testGetDefault() {
		Font font = FontManager.INSTANCE.getDefault();
		Assert.assertEquals(defaultFont, font);
		Assert.assertEquals(SWT.NONE, font.getFontData()[0].getStyle());
	}
	
	@Test 
	public void testGetItalic() {
		Font font = FontManager.INSTANCE.getItalic();
		FontData fontData = font.getFontData()[0];
		Assert.assertEquals(SWT.ITALIC, fontData.getStyle());
		Assert.assertEquals(defaultFontHeight, fontData.getHeight());
		Assert.assertEquals(defaultFontName, fontData.getName());
	}
	
	@Test 
	public void testGetBold() {
		Font font = FontManager.INSTANCE.getBold();
		FontData fontData = font.getFontData()[0];
		Assert.assertEquals(SWT.BOLD, fontData.getStyle());
		Assert.assertEquals(defaultFontHeight, fontData.getHeight());
		Assert.assertEquals(defaultFontName, fontData.getName());
	}
	
	@Test 
	public void testGetItalicBold() {
		Font font = FontManager.INSTANCE.getItalicBold();
		FontData fontData = font.getFontData()[0];
		Assert.assertEquals(SWT.BOLD | SWT.ITALIC, fontData.getStyle());
		Assert.assertEquals(defaultFontHeight, fontData.getHeight());
		Assert.assertEquals(defaultFontName, fontData.getName());
	}
	
}
