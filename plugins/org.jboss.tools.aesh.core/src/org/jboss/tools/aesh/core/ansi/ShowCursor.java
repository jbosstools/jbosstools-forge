package org.jboss.tools.aesh.core.ansi;


public class ShowCursor extends ControlSequence {

	public ShowCursor(String arguments) {}

	@Override
	public ControlSequenceType getType() {
		return ControlSequenceType.SHOW_CURSOR;
	}

}
