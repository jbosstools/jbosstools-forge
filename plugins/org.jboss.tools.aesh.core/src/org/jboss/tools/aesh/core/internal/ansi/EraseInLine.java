package org.jboss.tools.aesh.core.internal.ansi;

import org.jboss.tools.aesh.core.document.Document;


public class EraseInLine extends AbstractCommand {

	public EraseInLine(String arguments) {}

	@Override
	public CommandType getType() {
		return CommandType.ERASE_IN_LINE;
	}
	
	@Override
	public void handle(Document document) {
		document.replace(
				document.getCursorOffset(), 
				document.getLength() - document.getCursorOffset(), 
				"");
		document.setDefaultStyle();
	}

}
