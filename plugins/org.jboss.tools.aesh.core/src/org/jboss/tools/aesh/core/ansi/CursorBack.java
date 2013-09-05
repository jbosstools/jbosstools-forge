package org.jboss.tools.aesh.core.ansi;


public class CursorBack extends ControlSequence {

	public CursorBack(String controlSequenceString) {
		super(controlSequenceString);
	}

	@Override
	protected ControlSequenceType getType() {
		return ControlSequenceType.CURSOR_BACK;
	}

}
