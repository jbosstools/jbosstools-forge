package org.jboss.tools.aesh.core.internal.ansi;

import org.jboss.tools.aesh.core.document.Document;
import org.jboss.tools.aesh.core.internal.AeshCorePlugin;


public class CursorHorizontalAbsolute extends AbstractCommand {
	
	private int column = 0;

	public CursorHorizontalAbsolute(String arguments) {
		try {
			column = Integer.valueOf(arguments);
		} catch (NumberFormatException e) {
			AeshCorePlugin.log(e);
		}
	}

	@Override
	public CommandType getType() {
		return CommandType.CURSOR_HORIZONTAL_ABSOLUTE;
	}
	
	@Override
	public void handle(Document document) {
		int lineStart = document.getLineOffset(document.getLineOfOffset(document.getCursorOffset()));
		document.moveCursorTo(lineStart + column); 
	}

}
