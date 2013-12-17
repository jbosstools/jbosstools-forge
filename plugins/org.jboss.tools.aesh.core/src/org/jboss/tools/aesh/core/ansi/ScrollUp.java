package org.jboss.tools.aesh.core.ansi;


public class ScrollUp extends ControlSequence {

	public ScrollUp(String arguments) {}

	@Override
	public ControlSequenceType getType() {
		return ControlSequenceType.SCROLL_UP;
	}

}
