package org.jboss.tools.aesh.core.ansi;


public class RestoreCursorPosition extends ControlSequence {

	public RestoreCursorPosition(String controlSequenceString) {
		super(controlSequenceString);
	}

	@Override
	protected ControlSequenceType getType() {
		return ControlSequenceType.RESTORE_CURSOR_POSITION;
	}

}
