package org.jboss.tools.aesh.core.internal.ansi;

import org.jboss.tools.aesh.core.document.AeshDocument;


public class CursorBack extends AbstractAnsiControlSequence {
	
	private int amount;

	public CursorBack(String arguments) {
		amount = Integer.valueOf(arguments);
	}

	@Override
	public AnsiControlSequenceType getType() {
		return AnsiControlSequenceType.CURSOR_BACK;
	}
	
	@Override
	public void handle(AeshDocument document) {
		document.moveCursorTo(document.getCursorOffset() - amount);
	}

}
