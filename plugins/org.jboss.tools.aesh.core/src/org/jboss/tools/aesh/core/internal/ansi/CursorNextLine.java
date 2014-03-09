package org.jboss.tools.aesh.core.internal.ansi;



public class CursorNextLine extends AbstractCommand {

	public CursorNextLine(String arguments) {}

	@Override
	public ControlSequenceType getType() {
		return ControlSequenceType.CURSOR_NEXT_LINE;
	}

}
