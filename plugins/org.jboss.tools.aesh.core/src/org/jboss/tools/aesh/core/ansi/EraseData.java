package org.jboss.tools.aesh.core.ansi;

import org.jboss.tools.aesh.core.document.AeshDocument;


public class EraseData extends ControlSequence {
	
	private String arguments;

	public EraseData(String arguments) {
		this.arguments = arguments;
	}

	@Override
	public ControlSequenceType getType() {
		return ControlSequenceType.ERASE_DATA;
	}
	
	@Override
	public void handle(AeshDocument document) {
    	if ("2".equals(arguments)) {
    		document.reset();
    	}		
	}

}
