package org.jboss.tools.aesh.core.internal.ansi;

import org.jboss.tools.aesh.core.ansi.Document;


public class CursorHorizontalAbsolute extends AbstractCommand {
	
	private int column;

	public CursorHorizontalAbsolute(String arguments) {
		column = Integer.valueOf(arguments);
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
