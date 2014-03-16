package org.jboss.tools.aesh.ui.internal;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;

public class FontManager {
	
	public static final String AESH_CONSOLE_FONT = "org.jboss.tools.aesh.ui.font";

	public static FontManager INSTANCE = new FontManager();
	
	private static Font ITALIC;
	private static Font NORMAL;
	private static Font BOLD;
	private static Font ITALIC_BOLD;
	
	private FontManager() {
		initializeDefault();
		initializeItalic();
		initializeBold();
		initializeItalicBold();
	}
	
	private void initializeDefault() {
		NORMAL = JFaceResources.getFont(AESH_CONSOLE_FONT);
	}
	
	private FontData createFontDataFromNormal() {
		FontData normalData = NORMAL.getFontData()[0];
		FontData result = new FontData();
		result.setName(normalData.getName());
		result.height = normalData.height;
		return result;
	}
	
	private void initializeItalic() {
		FontData italicData = createFontDataFromNormal();
		italicData.setStyle(SWT.ITALIC);
		ITALIC = new Font(NORMAL.getDevice(), italicData);
	}
	
	private void initializeBold() {
		FontData boldData = createFontDataFromNormal();
		boldData.setStyle(SWT.BOLD);
		BOLD = new Font(NORMAL.getDevice(), boldData);
	}
	
	private void initializeItalicBold() {
		FontData italicBoldData = createFontDataFromNormal();
		italicBoldData.setStyle(SWT.BOLD | SWT.ITALIC);
		ITALIC_BOLD = new Font(NORMAL.getDevice(), italicBoldData);
	}
	
	public Font getDefault() {
		return NORMAL;
	}
	
	public Font getItalic() {
		return ITALIC;
	}
	
	public Font getBold() {
		return BOLD;
	}
	
	public Font getItalicBold() {
		return ITALIC_BOLD;
	}

}
