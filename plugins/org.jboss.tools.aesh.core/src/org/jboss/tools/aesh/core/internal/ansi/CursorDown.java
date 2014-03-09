package org.jboss.tools.aesh.core.internal.ansi;



public class CursorDown extends AbstractCommand {

	public CursorDown(String arguments) {}

	@Override
	public CommandType getType() {
		return CommandType.CURSOR_DOWN;
	}

}
