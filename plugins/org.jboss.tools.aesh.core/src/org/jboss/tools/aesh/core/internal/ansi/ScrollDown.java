package org.jboss.tools.aesh.core.internal.ansi;



public class ScrollDown extends AbstractCommand {

	public ScrollDown(String arguments) {}

	@Override
	public CommandType getType() {
		return CommandType.SCROLL_DOWN;
	}

}
