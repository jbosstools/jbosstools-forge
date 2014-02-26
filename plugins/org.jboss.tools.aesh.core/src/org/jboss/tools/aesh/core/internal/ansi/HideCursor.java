package org.jboss.tools.aesh.core.internal.ansi;



public class HideCursor extends AbstractAnsiControlSequence {

	public HideCursor(String arguments) {}

	@Override
	public AnsiControlSequenceType getType() {
		return AnsiControlSequenceType.HIDE_CURSOR;
	}

}
