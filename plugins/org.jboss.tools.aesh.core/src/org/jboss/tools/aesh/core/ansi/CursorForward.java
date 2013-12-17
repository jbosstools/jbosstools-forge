package org.jboss.tools.aesh.core.ansi;

import org.jboss.tools.aesh.core.document.DocumentProxy;


public class CursorForward extends ControlSequence {
	
	private int amount;

	public CursorForward(String arguments) {
		amount = Integer.valueOf(arguments);
	}

	@Override
	public ControlSequenceType getType() {
		return ControlSequenceType.CURSOR_FORWARD;
	}
	
	@Override
	public void handle(DocumentProxy document) {
		document.moveCursorTo(document.getCursorOffset() + amount);
	}

}
