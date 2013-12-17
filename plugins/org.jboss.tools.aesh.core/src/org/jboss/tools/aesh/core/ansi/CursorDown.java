package org.jboss.tools.aesh.core.ansi;


public class CursorDown extends ControlSequence {

	public CursorDown(String arguments) {}

	@Override
	public ControlSequenceType getType() {
		return ControlSequenceType.CURSOR_DOWN;
	}

}
