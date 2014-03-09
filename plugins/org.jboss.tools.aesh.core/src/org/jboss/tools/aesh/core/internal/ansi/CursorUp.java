package org.jboss.tools.aesh.core.internal.ansi;



public class CursorUp extends AbstractCommand {
	
	public CursorUp(String arguments) {}

	@Override
	public ControlSequenceType getType() {
		return ControlSequenceType.CURSOR_UP;
	}

}
