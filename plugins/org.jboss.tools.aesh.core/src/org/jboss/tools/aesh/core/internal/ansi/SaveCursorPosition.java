package org.jboss.tools.aesh.core.internal.ansi;

import org.jboss.tools.aesh.core.document.Document;


public class SaveCursorPosition extends AbstractCommand {

	public SaveCursorPosition(String arguments) {}

	@Override
	public CommandType getType() {
		return CommandType.SAVE_CURSOR_POSITION;
	}
	
	@Override
	public void handle(Document document) {
		document.saveCursor();
	}

}
