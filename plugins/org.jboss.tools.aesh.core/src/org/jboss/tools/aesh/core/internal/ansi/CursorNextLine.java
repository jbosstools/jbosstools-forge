package org.jboss.tools.aesh.core.internal.ansi;



public class CursorNextLine extends AbstractControlSequence {

	public CursorNextLine(String arguments) {}

	@Override
	public AnsiControlSequenceType getType() {
		return AnsiControlSequenceType.CURSOR_NEXT_LINE;
	}

}
