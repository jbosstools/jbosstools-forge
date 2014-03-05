package org.jboss.tools.aesh.core.internal.ansi;



public class ScrollUp extends AbstractControlSequence {

	public ScrollUp(String arguments) {}

	@Override
	public AnsiControlSequenceType getType() {
		return AnsiControlSequenceType.SCROLL_UP;
	}

}
