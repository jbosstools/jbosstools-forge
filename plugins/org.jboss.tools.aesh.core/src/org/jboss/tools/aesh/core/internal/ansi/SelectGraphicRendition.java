package org.jboss.tools.aesh.core.internal.ansi;

import java.util.StringTokenizer;

import org.jboss.tools.aesh.core.ansi.Document;
import org.jboss.tools.aesh.core.ansi.Style;
import org.jboss.tools.aesh.core.internal.AeshCorePlugin;


public class SelectGraphicRendition extends AbstractCommand {
	
	private String arguments;

	public SelectGraphicRendition(String arguments) {
		this.arguments = arguments;
	}

	@Override
	public CommandType getType() {
		return CommandType.SELECT_GRAPHIC_RENDITION;
	}
	
	@Override
	public void handle(Document document) {
		Style style = document.newStyleFromCurrent();
		StringTokenizer tokenizer = new StringTokenizer(arguments, ";");
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			if ("".equals(token)) continue;
			try {
				int value = Integer.valueOf(token);
				if (value == 38 || value == 48) {
					handleXTerm(value, tokenizer, style);
				} else {
					handleDefault(value, style);
				}
			} catch (NumberFormatException e) {
				AeshCorePlugin.log(e);
			}
		}
    	document.setCurrentStyle(style);
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
	
	private void handleDefault(int sgrCode, Style style) {
		switch(sgrCode) {
			case   0 : style.resetToNormal(); break;
			case   1 : style.setBoldOn(); break;
			case   2 : style.setFaintOn(); break;
 			case   3 : style.setItalicOn(); break;
 			case   4 : style.setUnderlineSingle(); break;
			case   7 : style.setImageNegative(); break;
			case   9 : style.setCrossedOut(); break;
			case  22 : style.setBoldOrFaintOff(); break;
			case  23 : style.setItalicOff(); break;
			case  24 : style.setUnderlineNone(); break;
			case  27 : style.setImagePositive(); break;
			case  29 : style.setNotCrossedOut(); break;
			case  30 : style.setForegroundBlack(); break;
			case  31 : style.setForegroundRed(); break;
			case  32 : style.setForegroundGreen(); break;
			case  33 : style.setForegroundYellow(); break;
			case  34 : style.setForegroundBlue(); break;
			case  35 : style.setForegroundMagenta(); break;
			case  36 : style.setForegroundCyan(); break;
			case  37 : style.setForegroundWhite(); break;
			case  39 : style.setForegroundDefault(); break;
			case  40 : style.setBackgroundBlack(); break;
			case  41 : style.setBackgroundRed(); break;
			case  42 : style.setBackgroundGreen(); break;
			case  43 : style.setBackgroundYellow();break;
			case  44 : style.setBackgroundBlue(); break;
			case  45 : style.setBackgroundMagenta(); break;
			case  46 : style.setBackgroundCyan(); break;
			case  47 : style.setBackgroundWhite(); break;
			case  49 : style.setBackgroundDefault(); break;
			default  : throw new RuntimeException("Unknown SGR code: " + sgrCode);
		}
	}
	
}
