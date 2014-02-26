package org.jboss.tools.aesh.core.internal.ansi;



public class ScrollDown extends AbstractAnsiControlSequence {

	public ScrollDown(String arguments) {}

	@Override
	public AnsiControlSequenceType getType() {
		return AnsiControlSequenceType.SCROLL_DOWN;
	}

}
