package org.jboss.tools.aesh.core.internal.ansi;



public class CursorPreviousLine extends AbstractCommand {

	public CursorPreviousLine(String arguments) {}

	@Override
	public CommandType getType() {
		return CommandType.CURSOR_PREVIOUS_LINE;
	}

}
