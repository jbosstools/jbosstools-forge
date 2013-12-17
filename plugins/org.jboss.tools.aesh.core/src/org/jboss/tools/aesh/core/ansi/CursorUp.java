package org.jboss.tools.aesh.core.ansi;


public class CursorUp extends ControlSequence {
	
	public CursorUp(String arguments) {}

	@Override
	public ControlSequenceType getType() {
		return ControlSequenceType.CURSOR_UP;
	}

}
