package org.jboss.tools.aesh.core.ansi;


public class CursorDown extends ControlSequence {

	public CursorDown(String controlSequenceString) {
		super(controlSequenceString);
	}

	@Override
	protected ControlSequenceType getType() {
		return ControlSequenceType.CURSOR_DOWN;
	}

}
