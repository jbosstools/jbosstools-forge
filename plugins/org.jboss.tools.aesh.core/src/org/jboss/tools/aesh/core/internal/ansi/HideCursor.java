package org.jboss.tools.aesh.core.internal.ansi;



public class HideCursor extends AbstractControlSequence {

	public HideCursor(String arguments) {}

	@Override
	public ControlSequenceType getType() {
		return ControlSequenceType.HIDE_CURSOR;
	}

}
