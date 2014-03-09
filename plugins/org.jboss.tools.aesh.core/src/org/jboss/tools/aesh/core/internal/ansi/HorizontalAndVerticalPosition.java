package org.jboss.tools.aesh.core.internal.ansi;



public class HorizontalAndVerticalPosition extends AbstractCommand {

	public HorizontalAndVerticalPosition(String arguments) {}

	@Override
	public ControlSequenceType getType() {
		return ControlSequenceType.HORIZONTAL_AND_VERTICAL_POSITION;
	}

}
