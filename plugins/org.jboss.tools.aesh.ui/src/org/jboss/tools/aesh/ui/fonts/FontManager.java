package org.jboss.tools.aesh.ui.fonts;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.jboss.tools.aesh.ui.AeshUIConstants;

public class FontManager {
	
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
		NORMAL = JFaceResources.getFont(AeshUIConstants.AESH_CONSOLE_FONT);
	}
	
	private FontData createFontDataFromNormal() {
		FontData normalData = NORMAL.getFontData()[0];
		FontData result = new FontData();
		result.name = normalData.name;
		result.height = normalData.height;
		return result;
	}
	
	private void initializeItalic() {
		FontData italicData = createFontDataFromNormal();
		italicData.style = SWT.ITALIC;
		ITALIC = new Font(NORMAL.getDevice(), italicData);
	}
	
	private void initializeBold() {
		FontData boldData = createFontDataFromNormal();
		boldData.style = SWT.BOLD;
		BOLD = new Font(NORMAL.getDevice(), boldData);
	}
	
	private void initializeItalicBold() {
		FontData italicBoldData = createFontDataFromNormal();
		italicBoldData.style = SWT.BOLD | SWT.ITALIC;
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
