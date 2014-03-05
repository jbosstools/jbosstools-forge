package org.jboss.tools.aesh.core.internal.ansi;

import org.jboss.tools.aesh.core.ansi.AnsiDocument;


public class RestoreCursorPosition extends AbstractControlSequence {

	public RestoreCursorPosition(String arguments) {}

	@Override
	public AnsiControlSequenceType getType() {
		return AnsiControlSequenceType.RESTORE_CURSOR_POSITION;
	}
	
	@Override
	public void handle(AnsiDocument document) {
		document.restoreCursor();
	}

}
