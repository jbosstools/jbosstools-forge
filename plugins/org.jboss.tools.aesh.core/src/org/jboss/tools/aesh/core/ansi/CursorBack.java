package org.jboss.tools.aesh.core.ansi;

import org.jboss.tools.aesh.core.document.DocumentProxy;


public class CursorBack extends ControlSequence {

	public CursorBack(String controlSequenceString) {
		super(controlSequenceString);
	}

	@Override
	public ControlSequenceType getType() {
		return ControlSequenceType.CURSOR_BACK;
	}
	
	@Override
	public void handle(DocumentProxy document) {
		String command = getControlSequenceString();
		int amount = Integer.valueOf(command.substring(2, command.length() - 1));
		int current = document.getCursorOffset();
		document.moveCursorTo(current - amount);
	}

}
