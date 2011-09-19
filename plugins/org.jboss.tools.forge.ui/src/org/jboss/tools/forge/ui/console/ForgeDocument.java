package org.jboss.tools.forge.ui.console;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.swt.widgets.Display;

public class ForgeDocument extends Document {
	
    public void appendString(final String str) {
    	Display.getDefault().asyncExec(new Runnable() {				
			@Override
			public void run() {
				try {
					for (int i = 0; i < str.length(); i++) {
						char c = str.charAt(i);
						if (c == '\r') continue; //ignore
						if (str.charAt(i) == '\b') {
							replace(getLength() - 1, 1, "");
						} else {
							replace(getLength(), 0, str.substring(i, i + 1));
						}
					}
				} catch (BadLocationException e) {}
			}
		});
    }

}
