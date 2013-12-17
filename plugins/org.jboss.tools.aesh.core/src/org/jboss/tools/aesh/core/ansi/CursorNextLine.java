package org.jboss.tools.aesh.core.ansi;


public class CursorNextLine extends ControlSequence {

	public CursorNextLine(String arguments) {}

	@Override
	public ControlSequenceType getType() {
		return ControlSequenceType.CURSOR_NEXT_LINE;
	}

}
