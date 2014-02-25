package org.jboss.tools.aesh.core.ansi;

import org.jboss.tools.aesh.core.document.Document;


public class CursorBack extends ControlSequence {
	
	private int amount;

	public CursorBack(String arguments) {
		amount = Integer.valueOf(arguments);
	}

	@Override
	public ControlSequenceType getType() {
		return ControlSequenceType.CURSOR_BACK;
	}
	
	@Override
	public void handle(Document document) {
		document.moveCursorTo(document.getCursorOffset() - amount);
	}

}
