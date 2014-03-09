package org.jboss.tools.aesh.core.internal.ansi;

import org.jboss.tools.aesh.core.ansi.Document;


public class SaveCursorPosition extends AbstractCommand {

	public SaveCursorPosition(String arguments) {}

	@Override
	public ControlSequenceType getType() {
		return ControlSequenceType.SAVE_CURSOR_POSITION;
	}
	
	@Override
	public void handle(Document document) {
		document.saveCursor();
	}

}
