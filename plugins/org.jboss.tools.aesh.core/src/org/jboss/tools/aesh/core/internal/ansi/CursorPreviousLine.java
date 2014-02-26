package org.jboss.tools.aesh.core.internal.ansi;



public class CursorPreviousLine extends AbstractAnsiControlSequence {

	public CursorPreviousLine(String arguments) {}

	@Override
	public AnsiControlSequenceType getType() {
		return AnsiControlSequenceType.CURSOR_PREVIOUS_LINE;
	}

}
