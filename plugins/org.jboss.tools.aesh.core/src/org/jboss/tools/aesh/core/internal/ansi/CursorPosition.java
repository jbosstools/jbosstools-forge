package org.jboss.tools.aesh.core.internal.ansi;

import org.jboss.tools.aesh.core.document.AeshDocument;


public class CursorPosition extends AbstractAnsiControlSequence {
	
	private int line, column = 0;

	public CursorPosition(String arguments) {
    	int i = arguments.indexOf(';');
    	if (i != -1) {
    		line = Integer.valueOf(arguments.substring(0, i));
    		column = Integer.valueOf(arguments.substring(i + 1));
    	} else if (arguments.length() > 0) {
    		line = Integer.valueOf(arguments);
    	}
	}

	@Override
	public AnsiControlSequenceType getType() {
		return AnsiControlSequenceType.CURSOR_POSITION;
	}
	
	@Override
	public void handle(AeshDocument document) {
    	int offset = document.getLineOffset(line);
    	int maxColumn = document.getLineLength(line);
    	offset += Math.min(maxColumn, column);
    	document.moveCursorTo(offset);
	}

}
