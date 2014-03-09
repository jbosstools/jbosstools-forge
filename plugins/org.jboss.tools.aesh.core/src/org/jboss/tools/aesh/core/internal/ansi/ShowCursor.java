package org.jboss.tools.aesh.core.internal.ansi;



public class ShowCursor extends AbstractCommand {

	public ShowCursor(String arguments) {}

	@Override
	public CommandType getType() {
		return CommandType.SHOW_CURSOR;
	}

}
