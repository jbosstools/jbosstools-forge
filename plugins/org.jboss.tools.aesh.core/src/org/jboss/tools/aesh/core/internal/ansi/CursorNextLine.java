package org.jboss.tools.aesh.core.internal.ansi;



public class CursorNextLine extends AbstractAnsiControlSequence {

	public CursorNextLine(String arguments) {}

	@Override
	public AnsiControlSequenceType getType() {
		return AnsiControlSequenceType.CURSOR_NEXT_LINE;
	}

}
