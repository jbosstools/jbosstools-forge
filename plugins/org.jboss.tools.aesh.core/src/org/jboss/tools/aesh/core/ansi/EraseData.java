package org.jboss.tools.aesh.core.ansi;

import org.jboss.tools.aesh.core.document.DocumentProxy;


public class EraseData extends ControlSequence {

	public EraseData(String controlSequenceString) {
		super(controlSequenceString);
	}

	@Override
	public ControlSequenceType getType() {
		return ControlSequenceType.ERASE_DATA;
	}
	
	@Override
	public void handle(DocumentProxy document) {
    	String command = getControlSequenceString();
    	String str = command.substring(2, command.length() - 1);
    	if ("2".equals(str)) {
    		document.reset();
    	}		
	}

}
