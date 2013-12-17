package org.jboss.tools.aesh.core.ansi;

import org.jboss.tools.aesh.core.document.DocumentProxy;


public class CursorPosition extends ControlSequence {

	public CursorPosition(String controlSequenceString) {
		super(controlSequenceString);
	}

	@Override
	public ControlSequenceType getType() {
		return ControlSequenceType.CURSOR_POSITION;
	}
	
	@Override
	public void handle(DocumentProxy document) {
    	String command = getControlSequenceString();
    	String str = command.substring(2, command.length() - 1);
    	int i = str.indexOf(';');
    	int line = 0, column = 0;
    	if (i != -1) {
    		line = Integer.valueOf(str.substring(0, i));
    		column = Integer.valueOf(str.substring(i + 1));
    	} else if (str.length() > 0) {
    		line = Integer.valueOf(str);
    	}
    	int offset = document.getLineOffset(line);
    	int maxColumn = document.getLineLength(line);
    	offset += Math.min(maxColumn, column);
    	document.moveCursorTo(offset);
	}

}
