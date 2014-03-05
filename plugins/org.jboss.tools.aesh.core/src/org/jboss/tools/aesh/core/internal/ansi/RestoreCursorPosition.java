package org.jboss.tools.aesh.core.internal.ansi;

import org.jboss.tools.aesh.core.ansi.Document;


public class RestoreCursorPosition extends AbstractControlSequence {

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
