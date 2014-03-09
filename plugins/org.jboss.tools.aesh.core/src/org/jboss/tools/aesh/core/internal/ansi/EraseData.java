package org.jboss.tools.aesh.core.internal.ansi;

import org.jboss.tools.aesh.core.ansi.Document;


public class EraseData extends AbstractCommand {
	
	private String arguments;

	public EraseData(String arguments) {
		this.arguments = arguments;
	}

	@Override
	public CommandType getType() {
		return CommandType.ERASE_DATA;
	}
	
	@Override
	public void handle(Document document) {
    	if ("2".equals(arguments)) {
    		document.reset();
    	}		
	}

}
