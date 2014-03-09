package org.jboss.tools.aesh.core.internal.ansi;



public class ScrollDown extends AbstractCommand {

	public ScrollDown(String arguments) {}

	@Override
	public ControlSequenceType getType() {
		return ControlSequenceType.SCROLL_DOWN;
	}

}
