package org.jboss.tools.aesh.core.ansi;


public class CursorForward extends ControlSequence {

	public CursorForward(String controlSequenceString) {
		super(controlSequenceString);
	}

	@Override
	protected ControlSequenceType getType() {
		return ControlSequenceType.CURSOR_FORWARD;
	}

}
