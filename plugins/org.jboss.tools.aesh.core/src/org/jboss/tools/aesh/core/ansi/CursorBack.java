package org.jboss.tools.aesh.core.ansi;

import org.jboss.tools.aesh.core.document.DocumentProxy;


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
	public void handle(DocumentProxy document) {
		document.moveCursorTo(document.getCursorOffset() - amount);
	}

}
