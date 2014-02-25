package org.jboss.tools.aesh.core.ansi;

import org.jboss.tools.aesh.core.document.Document;


public class RestoreCursorPosition extends ControlSequence {

	public RestoreCursorPosition(String arguments) {}

	@Override
	public ControlSequenceType getType() {
		return ControlSequenceType.RESTORE_CURSOR_POSITION;
	}
	
	@Override
	public void handle(Document document) {
		document.restoreCursor();
	}

}
