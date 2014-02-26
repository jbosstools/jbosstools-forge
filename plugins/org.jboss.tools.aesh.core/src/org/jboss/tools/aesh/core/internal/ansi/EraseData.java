package org.jboss.tools.aesh.core.internal.ansi;

import org.jboss.tools.aesh.core.ansi.AnsiDocument;


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
	public void handle(AnsiDocument document) {
    	if ("2".equals(arguments)) {
    		document.reset();
    	}		
	}

}
