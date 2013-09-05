package org.jboss.tools.aesh.core.ansi;


public class CursorUp extends ControlSequence {
	
	public CursorUp(String controlSequenceString) {
		super(controlSequenceString);
	}

	@Override
	protected ControlSequenceType getType() {
		return ControlSequenceType.CURSOR_UP;
	}

}
