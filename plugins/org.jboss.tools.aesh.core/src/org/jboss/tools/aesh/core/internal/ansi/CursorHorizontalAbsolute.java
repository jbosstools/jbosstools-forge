package org.jboss.tools.aesh.core.internal.ansi;

import org.jboss.tools.aesh.core.ansi.AnsiDocument;


public class CursorHorizontalAbsolute extends AbstractControlSequence {
	
	private int column;

	public CursorHorizontalAbsolute(String arguments) {
		column = Integer.valueOf(arguments);
	}

	@Override
	public AnsiControlSequenceType getType() {
		return AnsiControlSequenceType.CURSOR_HORIZONTAL_ABSOLUTE;
	}
	
	@Override
	public void handle(AnsiDocument document) {
		int lineStart = document.getLineOffset(document.getLineOfOffset(document.getCursorOffset()));
		document.moveCursorTo(lineStart + column); 
	}

}
