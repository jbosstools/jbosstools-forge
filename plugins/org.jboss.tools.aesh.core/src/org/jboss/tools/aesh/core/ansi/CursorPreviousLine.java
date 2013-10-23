package org.jboss.tools.aesh.core.ansi;


public class CursorPreviousLine extends ControlSequence {

	public CursorPreviousLine(String controlSequenceString) {
		super(controlSequenceString);
	}

	@Override
	public ControlSequenceType getType() {
		return ControlSequenceType.CURSOR_PREVIOUS_LINE;
	}

}
