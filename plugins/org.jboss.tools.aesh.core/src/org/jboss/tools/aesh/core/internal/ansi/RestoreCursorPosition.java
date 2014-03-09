package org.jboss.tools.aesh.core.internal.ansi;

import org.jboss.tools.aesh.core.ansi.Document;


public class RestoreCursorPosition extends AbstractCommand {

	public RestoreCursorPosition(String arguments) {}

	@Override
	public CommandType getType() {
		return CommandType.RESTORE_CURSOR_POSITION;
	}
	
	@Override
	public void handle(Document document) {
		document.restoreCursor();
	}

}
