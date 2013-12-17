package org.jboss.tools.aesh.core.ansi;

import org.jboss.tools.aesh.core.document.DocumentProxy;


public class CursorPosition extends ControlSequence {
	
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
	public ControlSequenceType getType() {
		return ControlSequenceType.CURSOR_POSITION;
	}
	
	@Override
	public void handle(DocumentProxy document) {
    	int offset = document.getLineOffset(line);
    	int maxColumn = document.getLineLength(line);
    	offset += Math.min(maxColumn, column);
    	document.moveCursorTo(offset);
	}

}
