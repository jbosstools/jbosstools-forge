package org.jboss.tools.aesh.core.internal.ansi;



public class CursorPreviousLine extends AbstractCommand {

	public CursorPreviousLine(String arguments) {}

	@Override
	public ControlSequenceType getType() {
		return ControlSequenceType.CURSOR_PREVIOUS_LINE;
	}

}
