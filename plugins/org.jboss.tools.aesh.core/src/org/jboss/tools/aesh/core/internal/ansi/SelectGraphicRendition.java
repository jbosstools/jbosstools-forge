package org.jboss.tools.aesh.core.internal.ansi;

import java.util.StringTokenizer;

import org.jboss.tools.aesh.core.document.Document;
import org.jboss.tools.aesh.core.document.Style;
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
		boolean changeStyle = true;
		StringTokenizer tokenizer = new StringTokenizer(arguments, ";");
		int counter = 0;
		while (tokenizer.hasMoreTokens() && changeStyle) {
			String token = tokenizer.nextToken();
			if ("".equals(token)) continue;
			try {
				int value = Integer.valueOf(token);
				if (value == 38 || value == 48) {
					changeStyle = handleXTerm(value, tokenizer, style);
				} else {
					changeStyle = handleDefault(value, style);
				}
				counter++;
			} catch (NumberFormatException e) {
				AeshCorePlugin.log(e);
				changeStyle = false;
			}
		}
		if (changeStyle && counter > 0) {
			document.setCurrentStyle(style);
		}
	}
	
	private boolean handleXTerm(
			int sgrCode,
			StringTokenizer tokenizer, 
			Style style) {
		if (tokenizer.hasMoreTokens()) {
			String str = tokenizer.nextToken();
			try {
				int value = Integer.valueOf(str);
				if (value == 5) {
					if (tokenizer.hasMoreTokens()) {
						int code = Integer.valueOf(tokenizer.nextToken());
						if (sgrCode == 38) {
							style.setForegroundXTerm(code);
						} else if (sgrCode == 48) {
							style.setBackgroundXTerm(code);
						}
						return true;
					} else {
						AeshCorePlugin.log(new RuntimeException("Incorrect SGR instruction: " + arguments));
						return false;
					}
				} else {
					AeshCorePlugin.log(new RuntimeException("Incorrect SGR instruction: " + arguments));
					return false;
				}
			} catch (NumberFormatException e) {
				AeshCorePlugin.log(e);
				return false;
			}
		} else {
			AeshCorePlugin.log(new RuntimeException("Incorrect SGR instruction: " + arguments));
			return false;
		}
	}
	
	private boolean handleDefault(int sgrCode, Style style) {
		switch(sgrCode) {
			case   0 : style.resetToNormal(); return true;
			case   1 : style.setBoldOn(); return true;
			case   2 : style.setFaintOn(); return true;
 			case   3 : style.setItalicOn(); return true;
 			case   4 : style.setUnderlineSingle(); return true;
			case   7 : style.setImageNegative(); return true;
			case   9 : style.setCrossedOut(); return true;
			case  22 : style.setBoldOrFaintOff(); return true;
			case  23 : style.setItalicOff(); return true;
			case  24 : style.setUnderlineNone(); return true;
			case  27 : style.setImagePositive(); return true;
			case  29 : style.setNotCrossedOut(); return true;
			case  30 : style.setForegroundBlack(); return true;
			case  31 : style.setForegroundRed(); return true;
			case  32 : style.setForegroundGreen(); return true;
			case  33 : style.setForegroundYellow(); return true;
			case  34 : style.setForegroundBlue(); return true;
			case  35 : style.setForegroundMagenta(); return true;
			case  36 : style.setForegroundCyan(); return true;
			case  37 : style.setForegroundWhite(); return true;
			case  39 : style.setForegroundDefault(); return true;
			case  40 : style.setBackgroundBlack(); return true;
			case  41 : style.setBackgroundRed(); return true;
			case  42 : style.setBackgroundGreen(); return true;
			case  43 : style.setBackgroundYellow();return true;
			case  44 : style.setBackgroundBlue(); return true;
			case  45 : style.setBackgroundMagenta(); return true;
			case  46 : style.setBackgroundCyan(); return true;
			case  47 : style.setBackgroundWhite(); return true;
			case  49 : style.setBackgroundDefault(); return true;
			default  : AeshCorePlugin.log(new RuntimeException("Unknown SGR code: " + sgrCode)); return false;
		}
	}
	
}
