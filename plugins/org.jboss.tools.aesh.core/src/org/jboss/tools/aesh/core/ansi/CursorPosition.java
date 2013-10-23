package org.jboss.tools.aesh.core.ansi;


public class CursorPosition extends ControlSequence {

	public CursorPosition(String controlSequenceString) {
		super(controlSequenceString);
	}

	@Override
	public ControlSequenceType getType() {
		return ControlSequenceType.CURSOR_POSITION;
	}

}
