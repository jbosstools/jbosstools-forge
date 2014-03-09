package org.jboss.tools.aesh.core.internal.ansi;

import org.jboss.tools.aesh.core.ansi.Document;


public class CursorForward extends AbstractCommand {
	
	private int amount;

	public CursorForward(String arguments) {
		amount = Integer.valueOf(arguments);
	}

	@Override
	public CommandType getType() {
		return CommandType.CURSOR_FORWARD;
	}
	
	@Override
	public void handle(Document document) {
		document.moveCursorTo(document.getCursorOffset() + amount);
	}

}
