package org.jboss.tools.aesh.core.internal.ansi;



public class CursorDown extends AbstractCommand {

	public CursorDown(String arguments) {}

	@Override
	public ControlSequenceType getType() {
		return ControlSequenceType.CURSOR_DOWN;
	}

}
