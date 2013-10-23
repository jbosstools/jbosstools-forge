package org.jboss.tools.aesh.core.ansi;


public class HorizontalAndVerticalPosition extends ControlSequence {

	public HorizontalAndVerticalPosition(String controlSequenceString) {
		super(controlSequenceString);
	}

	@Override
	public ControlSequenceType getType() {
		return ControlSequenceType.HORIZONTAL_AND_VERTICAL_POSITION;
	}

}
