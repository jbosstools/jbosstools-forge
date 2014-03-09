package org.jboss.tools.aesh.core.internal.ansi;

import java.util.StringTokenizer;

import org.jboss.tools.aesh.core.ansi.Document;
import org.jboss.tools.aesh.core.ansi.Style;
import org.jboss.tools.aesh.core.internal.AeshCorePlugin;


public class SelectGraphicRendition extends AbstractControlSequence {
	
	private String arguments;

	public SelectGraphicRendition(String arguments) {
		this.arguments = arguments;
	}

	@Override
	public ControlSequenceType getType() {
		return ControlSequenceType.SELECT_GRAPHIC_RENDITION;
	}
	
	@Override
	public void handle(Document document) {
		Style styleRange = document.newStyleRangeFromCurrent();
		StringTokenizer tokenizer = new StringTokenizer(arguments, ";");
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			if ("".equals(token)) continue;
			try {
				int value = Integer.valueOf(token);
				if (value == 38 || value == 48) {
					handleXTerm(value, tokenizer, styleRange);
				} else {
					handleDefault(value, styleRange);
				}
			} catch (NumberFormatException e) {
				AeshCorePlugin.log(e);
			}
		}
    	document.setCurrentStyleRange(styleRange);
	}
	
	private void handleXTerm(
			int sgrCode,
			StringTokenizer tokenizer, 
			Style styleRange) {
		if (tokenizer.hasMoreTokens()) {
			String str = tokenizer.nextToken();
			try {
				int value = Integer.valueOf(str);
				if (value == 5) {
					if (tokenizer.hasMoreTokens()) {
						int code = Integer.valueOf(tokenizer.nextToken());
						if (sgrCode == 38) {
							styleRange.setForegroundXTerm(code);
						} else if (sgrCode == 48) {
							styleRange.setBackgroundXTerm(code);
						}
					} else {
						AeshCorePlugin.log(new RuntimeException("Incorrect SGR instruction: " + arguments));
					}
				} else {
					AeshCorePlugin.log(new RuntimeException("Incorrect SGR instruction: " + arguments));
				}
			} catch (NumberFormatException e) {
				AeshCorePlugin.log(e);
			}
		} else {
			AeshCorePlugin.log(new RuntimeException("Incorrect SGR instruction: " + arguments));
		}
	}
	
	private void handleDefault(int sgrCode, Style styleRange) {
		switch(sgrCode) {
			case   0 : styleRange.resetToNormal(); break;
			case   1 : styleRange.setBoldOn(); break;
			case   2 : styleRange.setFaintOn(); break;
 			case   3 : styleRange.setItalicOn(); break;
 			case   4 : styleRange.setUnderlineSingle(); break;
			case   7 : styleRange.setImageNegative(); break;
			case   9 : styleRange.setCrossedOut(); break;
			case  22 : styleRange.setBoldOrFaintOff(); break;
			case  23 : styleRange.setItalicOff(); break;
			case  24 : styleRange.setUnderlineNone(); break;
			case  27 : styleRange.setImagePositive(); break;
			case  29 : styleRange.setNotCrossedOut(); break;
			case  30 : styleRange.setForegroundBlack(); break;
			case  31 : styleRange.setForegroundRed(); break;
			case  32 : styleRange.setForegroundGreen(); break;
			case  33 : styleRange.setForegroundYellow(); break;
			case  34 : styleRange.setForegroundBlue(); break;
			case  35 : styleRange.setForegroundMagenta(); break;
			case  36 : styleRange.setForegroundCyan(); break;
			case  37 : styleRange.setForegroundWhite(); break;
			case  39 : styleRange.setForegroundDefault(); break;
			case  40 : styleRange.setBackgroundBlack(); break;
			case  41 : styleRange.setBackgroundRed(); break;
			case  42 : styleRange.setBackgroundGreen(); break;
			case  43 : styleRange.setBackgroundYellow();break;
			case  44 : styleRange.setBackgroundBlue(); break;
			case  45 : styleRange.setBackgroundMagenta(); break;
			case  46 : styleRange.setBackgroundCyan(); break;
			case  47 : styleRange.setBackgroundWhite(); break;
			case  49 : styleRange.setBackgroundDefault(); break;
			default  : throw new RuntimeException("Unknown SGR code: " + sgrCode);
		}
	}
	
}
