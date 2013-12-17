package org.jboss.tools.aesh.core.ansi;


public class CursorPreviousLine extends ControlSequence {

	public CursorPreviousLine(String arguments) {}

	@Override
	public ControlSequenceType getType() {
		return ControlSequenceType.CURSOR_PREVIOUS_LINE;
	}

}
