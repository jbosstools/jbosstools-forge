package org.jboss.tools.aesh.core.internal.ansi;



public class CursorUp extends AbstractAnsiControlSequence {
	
	public CursorUp(String arguments) {}

	@Override
	public AnsiControlSequenceType getType() {
		return AnsiControlSequenceType.CURSOR_UP;
	}

}