package org.jboss.tools.aesh.core.ansi;

import org.jboss.tools.aesh.core.document.DocumentProxy;


public class RestoreCursorPosition extends ControlSequence {

	public RestoreCursorPosition(String controlSequenceString) {
		super(controlSequenceString);
	}

	@Override
	public ControlSequenceType getType() {
		return ControlSequenceType.RESTORE_CURSOR_POSITION;
	}
	
	@Override
	public void handle(DocumentProxy document) {
		document.restoreCursor();
	}

}
