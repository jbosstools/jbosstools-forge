package org.jboss.tools.aesh.core.internal.ansi;



public class HideCursor extends AbstractCommand {

	public HideCursor(String arguments) {}

	@Override
	public CommandType getType() {
		return CommandType.HIDE_CURSOR;
	}

}
