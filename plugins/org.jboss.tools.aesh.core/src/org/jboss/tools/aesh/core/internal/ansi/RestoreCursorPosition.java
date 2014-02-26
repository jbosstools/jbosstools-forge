package org.jboss.tools.aesh.core.internal.ansi;

import org.jboss.tools.aesh.core.document.AeshDocument;


public class RestoreCursorPosition extends AbstractAnsiControlSequence {

	public RestoreCursorPosition(String arguments) {}

	@Override
	public AnsiControlSequenceType getType() {
		return AnsiControlSequenceType.RESTORE_CURSOR_POSITION;
	}
	
	@Override
	public void handle(AeshDocument document) {
		document.restoreCursor();
	}

}
