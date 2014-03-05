package org.jboss.tools.aesh.core.internal.ansi;



public class HorizontalAndVerticalPosition extends AbstractControlSequence {

	public HorizontalAndVerticalPosition(String arguments) {}

	@Override
	public AnsiControlSequenceType getType() {
		return AnsiControlSequenceType.HORIZONTAL_AND_VERTICAL_POSITION;
	}

}
