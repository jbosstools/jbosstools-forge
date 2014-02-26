package org.jboss.tools.aesh.core.internal.ansi;

import org.jboss.tools.aesh.core.ansi.AnsiDocument;


public class EraseInLine extends AbstractAnsiControlSequence {

	public EraseInLine(String arguments) {}

	@Override
	public AnsiControlSequenceType getType() {
		return AnsiControlSequenceType.ERASE_IN_LINE;
	}
	
	@Override
	public void handle(AnsiDocument document) {
		document.replace(
				document.getCursorOffset(), 
				document.getLength() - document.getCursorOffset(), 
				"");
		document.setDefaultStyleRange();
	}

}
