package org.jboss.tools.aesh.core.internal.ansi;



public class ScrollUp extends AbstractAnsiControlSequence {

	public ScrollUp(String arguments) {}

	@Override
	public AnsiControlSequenceType getType() {
		return AnsiControlSequenceType.SCROLL_UP;
	}

}
