package org.jboss.tools.aesh.core.internal.ansi;



public class CursorPreviousLine extends AbstractControlSequence {

	public CursorPreviousLine(String arguments) {}

	@Override
	public ControlSequenceType getType() {
		return ControlSequenceType.CURSOR_PREVIOUS_LINE;
	}

}
