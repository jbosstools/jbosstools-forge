package org.jboss.tools.aesh.ui.internal.document;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.TextStyle;
import org.jboss.tools.aesh.core.document.Style;
import org.jboss.tools.aesh.ui.internal.util.ColorConstants;
import org.jboss.tools.aesh.ui.internal.util.FontManager;

public class DelegatingStyleRange implements Style {
	
	private StyleRange styleRange;
	
	public DelegatingStyleRange(StyleRange styleRange) {
		this.styleRange = styleRange;
	}
	
	StyleRange getStyleRange() {
		return styleRange;
	}
	
	// 0
	public void resetToNormal() {
		styleRange.font = FontManager.INSTANCE.getDefault();
		styleRange.foreground = ColorConstants.BLACK;
		styleRange.background = ColorConstants.WHITE;		
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
	
	// 22
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
	
	// 30
	public void setForegroundBlack() {
		styleRange.foreground = ColorConstants.BLACK;
	}
	
	// 31
	public void setForegroundRed() {
		styleRange.foreground = ColorConstants.RED;
	}
	
	// 32
	public void setForegroundGreen() {
		styleRange.foreground = ColorConstants.GREEN;
	}
	
	// 33
	public void setForegroundYellow() {
		styleRange.foreground =ColorConstants.YELLOW;
	}
	
	// 34
	public void setForegroundBlue() {
		styleRange.foreground = ColorConstants.BLUE;
	}
	
	// 35
	public void setForegroundMagenta() {
		styleRange.foreground = ColorConstants.MAGENTA;
	}
	
	// 36
	public void setForegroundCyan() {
		styleRange.foreground = ColorConstants.CYAN;
	}
	
	// 37
	public void setForegroundWhite() {
		styleRange.foreground = ColorConstants.WHITE;
	}
	
	// 38
	public void setForegroundXTerm(int colour) {
	}
	
	// 39
	public void setForegroundDefault() {
		styleRange.foreground = ColorConstants.DEFAULT_FOREGROUND;
	}
	
	// 40
	public void setBackgroundBlack() {
		styleRange.background = ColorConstants.BLACK;
	}
	
	// 41
	public void setBackgroundRed() {
		styleRange.background = ColorConstants.RED;
	}
	
	// 42
	public void setBackgroundGreen() {
		styleRange.background = ColorConstants.GREEN;
	}
	
	// 43
	public void setBackgroundYellow() {
		styleRange.background = ColorConstants.YELLOW;
	}
	
	// 44
	public void setBackgroundBlue() {
		styleRange.background = ColorConstants.BLUE;
	}
	
	// 45
	public void setBackgroundMagenta() {
		styleRange.background = ColorConstants.MAGENTA;
	}
	
	// 46
	public void setBackgroundCyan() {
		styleRange.background = ColorConstants.CYAN;
	}
	
	// 47
	public void setBackgroundWhite() {
		styleRange.background = ColorConstants.WHITE;
	}
	
	// 48
	public void setBackgroundXTerm(int colour) {
	}
	
	// 49
	public void setBackgroundDefault() {
		styleRange.background = ColorConstants.DEFAULT_BACKGROUND;
	}
	
	public void setLength(int length) {
		styleRange.length = length;
	}
	
	public int getLength() {
		return styleRange.length;
	}
	
	private void reverseVideo() {
		Color foreground = styleRange.foreground;
		Color background = styleRange.background;
		styleRange.background = foreground;
		styleRange.foreground = background;
	}
	
	public static DelegatingStyleRange getDefault() {
		Font font = FontManager.INSTANCE.getDefault();
		Color foreground = ColorConstants.BLACK;
		Color background = ColorConstants.WHITE;
		TextStyle textStyle = new TextStyle(font, foreground, background);
		StyleRange styleRange = new StyleRange(textStyle);
		return new DelegatingStyleRange(styleRange);
	}
	
	public StyleRange getDelegate() {
		return styleRange;
	}
	
}
