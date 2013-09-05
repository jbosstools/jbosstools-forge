package org.jboss.tools.aesh.core.ansi;


public class ScrollUp extends ControlSequence {

	public ScrollUp(String controlSequenceString) {
		super(controlSequenceString);
	}

	@Override
	protected ControlSequenceType getType() {
		return ControlSequenceType.SCROLL_UP;
	}

}
