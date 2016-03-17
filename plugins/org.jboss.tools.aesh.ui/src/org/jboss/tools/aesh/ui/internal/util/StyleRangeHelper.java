package org.jboss.tools.aesh.ui.internal.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;

public class StyleRangeHelper {

	private StyleRangeHelper() {
	}

	public static StyleRange updateStyleRange(StyleRange styleRange) {
		if (styleRange.fontStyle == SWT.NORMAL) {
			styleRange.font = FontManager.INSTANCE.getDefault();
		} else if (styleRange.fontStyle == SWT.BOLD) {
			styleRange.font = FontManager.INSTANCE.getBold();
		} else if (styleRange.fontStyle == SWT.ITALIC) {
			styleRange.font = FontManager.INSTANCE.getItalic();
		} else if (styleRange.fontStyle == (SWT.BOLD | SWT.ITALIC)) {
			styleRange.font = FontManager.INSTANCE.getItalicBold();
		}
		return styleRange;
	}

}
