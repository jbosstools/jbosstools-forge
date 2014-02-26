package org.jboss.tools.aesh.core.internal.ansi;



public class CursorDown extends AbstractAnsiControlSequence {

	public CursorDown(String arguments) {}

	@Override
	public AnsiControlSequenceType getType() {
		return AnsiControlSequenceType.CURSOR_DOWN;
	}

}
