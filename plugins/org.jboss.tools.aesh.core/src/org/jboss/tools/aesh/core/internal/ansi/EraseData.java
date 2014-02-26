package org.jboss.tools.aesh.core.internal.ansi;

import org.jboss.tools.aesh.core.document.AeshDocument;


public class EraseData extends AbstractAnsiControlSequence {
	
	private String arguments;

	public EraseData(String arguments) {
		this.arguments = arguments;
	}

	@Override
	public AnsiControlSequenceType getType() {
		return AnsiControlSequenceType.ERASE_DATA;
	}
	
	@Override
	public void handle(AeshDocument document) {
    	if ("2".equals(arguments)) {
    		document.reset();
    	}		
	}

}
