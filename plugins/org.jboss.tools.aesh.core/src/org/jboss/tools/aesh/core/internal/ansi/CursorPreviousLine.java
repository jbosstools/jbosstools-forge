package org.jboss.tools.aesh.core.internal.ansi;



public class CursorPreviousLine extends AbstractControlSequence {

	public CursorPreviousLine(String arguments) {}

	@Override
	public AnsiControlSequenceType getType() {
		return AnsiControlSequenceType.CURSOR_PREVIOUS_LINE;
	}

}
