package org.jboss.tools.aesh.ui.document;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.jboss.tools.aesh.core.document.StyleRangeProxy;
import org.jboss.tools.aesh.ui.fonts.FontManager;

public class AeshStyleRangeProxy implements StyleRangeProxy {
	
	private StyleRange styleRange;
	
	public AeshStyleRangeProxy(StyleRange styleRange) {
		this.styleRange = styleRange;
	}
	
	public StyleRange getStyleRange() {
		return styleRange;
	}
	
	// 0
	public void resetToNormal() {
		styleRange.font = FontManager.INSTANCE.getDefault();
		styleRange.foreground = AeshColor.BLACK_TEXT.getColor();
		styleRange.background = AeshColor.WHITE_BG.getColor();		
	}
	
	// 1
	public void setBoldOn() {
		if (FontManager.INSTANCE.getDefault() == styleRange.font) {
			styleRange.font = FontManager.INSTANCE.getBold();
		} else if (FontManager.INSTANCE.getItalic() == styleRange.font) {
			styleRange.font = FontManager.INSTANCE.getItalicBold();
		}
	}
	
	// 2
	public void setFaintOn() {
		// faint is not supported as a SWT font style
		// this can be done by using a fainter variant of the same color
		// ignored for now
	}
	
	// 3
	public void setItalicOn() {
		if (FontManager.INSTANCE.getDefault() == styleRange.font) { 
			styleRange.font = FontManager.INSTANCE.getItalic();
		} else if (FontManager.INSTANCE.getBold() == styleRange.font) {
			styleRange.font = FontManager.INSTANCE.getItalicBold();
		}
	}
	
	// 4
	public void setUnderlineSingle() {
		styleRange.underline = true;
		styleRange.underlineStyle = SWT.SINGLE;
	}
	
	// 7
	public void setImageNegative() {
		reverseVideo();
	}
	
	// 9
	public void setCrossedOut() {
		styleRange.strikeout = true;
	}
	
	public void setBoldOrFaintOff() {
		if (FontManager.INSTANCE.getBold() == styleRange.font) {
			styleRange.font = FontManager.INSTANCE.getDefault();
		} else if (FontManager.INSTANCE.getItalicBold() == styleRange.font) {
			styleRange.font = FontManager.INSTANCE.getItalic();
		}
	}
	
	// 23
	public void setItalicOff() {
		if (FontManager.INSTANCE.getItalic() == styleRange.font) {
			styleRange.font = FontManager.INSTANCE.getDefault();
		} else if (FontManager.INSTANCE.getItalicBold() == styleRange.font) {
			styleRange.font = FontManager.INSTANCE.getBold();
		}
	}
	
	// 24
	public void setUnderlineNone() {
		styleRange.underline = false;
		styleRange.underlineStyle = SWT.NONE;
	}
	
	// 27
	public void setImagePositive() {
		reverseVideo();
	}
	
	// 29
	public void setNotCrossedOut() {
		styleRange.strikeout = false;
	}
	
	// 39
	public void setForegroundDefault() {
		styleRange.foreground = AeshColor.DEFAULT_TEXT.getColor();
	}
	
	// 41
	public void setBackgroundRed() {
		styleRange.background = AeshColor.RED_BG.getColor();
	}
	
	// 44
	public void setBackgroundBlue() {
		styleRange.background = AeshColor.BLUE_BG.getColor();
	}
	
	// 47
	public void setBackgroundWhite() {
		styleRange.background = AeshColor.WHITE_BG.getColor();
	}
	
	// 49
	public void setBackgroundDefault() {
		styleRange.background = AeshColor.WHITE_BG.getColor();
	}
	
	private void reverseVideo() {
		Color foreground = styleRange.foreground;
		Color background = styleRange.background;
		styleRange.background = foreground;
		styleRange.foreground = background;
	}
	
}
