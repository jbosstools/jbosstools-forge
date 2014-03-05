package org.jboss.tools.aesh.core.internal.ansi;

import org.jboss.tools.aesh.core.ansi.AnsiDocument;


public class CursorForward extends AbstractControlSequence {
	
	private int amount;

	public CursorForward(String arguments) {
		amount = Integer.valueOf(arguments);
	}

	@Override
	public ControlSequenceType getType() {
		return ControlSequenceType.CURSOR_FORWARD;
	}
	
	@Override
	public void handle(AnsiDocument document) {
		document.moveCursorTo(document.getCursorOffset() + amount);
	}

}
