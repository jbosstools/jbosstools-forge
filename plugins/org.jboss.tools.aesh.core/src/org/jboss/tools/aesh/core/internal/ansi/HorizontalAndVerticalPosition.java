package org.jboss.tools.aesh.core.internal.ansi;



public class HorizontalAndVerticalPosition extends AbstractCommand {

	public HorizontalAndVerticalPosition(String arguments) {}

	@Override
	public CommandType getType() {
		return CommandType.HORIZONTAL_AND_VERTICAL_POSITION;
	}

}
