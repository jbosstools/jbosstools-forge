package org.jboss.tools.aesh.core.ansi;

import org.jboss.tools.aesh.core.document.DocumentProxy;


public class SaveCursorPosition extends ControlSequence {

	public SaveCursorPosition(String arguments) {}

	@Override
	public ControlSequenceType getType() {
		return ControlSequenceType.SAVE_CURSOR_POSITION;
	}
	
	@Override
	public void handle(DocumentProxy document) {
		document.saveCursor();
	}

}
