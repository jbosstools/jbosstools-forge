package org.jboss.tools.aesh.core.internal.ansi;

import org.jboss.tools.aesh.core.ansi.AnsiDocument;


public class CursorBack extends AbstractControlSequence {
	
	private int amount;

	public CursorBack(String arguments) {
		amount = Integer.valueOf(arguments);
	}

	@Override
	public AnsiControlSequenceType getType() {
		return AnsiControlSequenceType.CURSOR_BACK;
	}
	
	@Override
	public void handle(AnsiDocument document) {
		document.moveCursorTo(document.getCursorOffset() - amount);
	}

}
