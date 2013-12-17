package org.jboss.tools.aesh.core.ansi;


public class ScrollDown extends ControlSequence {

	public ScrollDown(String arguments) {}

	@Override
	public ControlSequenceType getType() {
		return ControlSequenceType.SCROLL_DOWN;
	}

}
