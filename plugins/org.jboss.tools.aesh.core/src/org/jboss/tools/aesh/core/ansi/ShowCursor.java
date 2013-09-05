package org.jboss.tools.aesh.core.ansi;


public class ShowCursor extends ControlSequence {

	public ShowCursor(String controlSequenceString) {
		super(controlSequenceString);
	}

	@Override
	protected ControlSequenceType getType() {
		return ControlSequenceType.SHOW_CURSOR;
	}

}
