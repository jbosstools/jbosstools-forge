package org.jboss.tools.aesh.core.internal.ansi;



public class ScrollUp extends AbstractCommand {

	public ScrollUp(String arguments) {}

	@Override
	public CommandType getType() {
		return CommandType.SCROLL_UP;
	}

}
