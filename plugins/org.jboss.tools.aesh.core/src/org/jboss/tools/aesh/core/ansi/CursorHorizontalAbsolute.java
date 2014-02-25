package org.jboss.tools.aesh.core.ansi;

import org.jboss.tools.aesh.core.document.AeshDocument;


public class CursorHorizontalAbsolute extends ControlSequence {
	
	private int column;

	public CursorHorizontalAbsolute(String arguments) {
		column = Integer.valueOf(arguments);
	}

	@Override
	public ControlSequenceType getType() {
		return ControlSequenceType.CURSOR_HORIZONTAL_ABSOLUTE;
	}
	
	@Override
	public void handle(AeshDocument document) {
		int lineStart = document.getLineOffset(document.getLineOfOffset(document.getCursorOffset()));
		document.moveCursorTo(lineStart + column); 
	}

}
