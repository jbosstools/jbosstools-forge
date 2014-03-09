package org.jboss.tools.aesh.core.internal.ansi;



public class CursorNextLine extends AbstractCommand {

	public CursorNextLine(String arguments) {}

	@Override
	public CommandType getType() {
		return CommandType.CURSOR_NEXT_LINE;
	}

}
