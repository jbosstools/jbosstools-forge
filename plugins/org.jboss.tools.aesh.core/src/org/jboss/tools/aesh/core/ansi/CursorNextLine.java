package org.jboss.tools.aesh.core.ansi;


public class CursorNextLine extends ControlSequence {

	public CursorNextLine(String controlSequenceString) {
		super(controlSequenceString);
	}

	@Override
	protected ControlSequenceType getType() {
		return ControlSequenceType.CURSOR_NEXT_LINE;
	}

}
