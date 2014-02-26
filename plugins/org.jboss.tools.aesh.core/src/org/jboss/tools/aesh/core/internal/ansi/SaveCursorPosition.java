package org.jboss.tools.aesh.core.internal.ansi;

import org.jboss.tools.aesh.core.ansi.AnsiDocument;


public class SaveCursorPosition extends AbstractAnsiControlSequence {

	public SaveCursorPosition(String arguments) {}

	@Override
	public AnsiControlSequenceType getType() {
		return AnsiControlSequenceType.SAVE_CURSOR_POSITION;
	}
	
	@Override
	public void handle(AnsiDocument document) {
		document.saveCursor();
	}

}
