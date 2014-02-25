package org.jboss.tools.aesh.core.ansi;

import org.jboss.tools.aesh.core.document.Document;


public class SaveCursorPosition extends ControlSequence {

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
