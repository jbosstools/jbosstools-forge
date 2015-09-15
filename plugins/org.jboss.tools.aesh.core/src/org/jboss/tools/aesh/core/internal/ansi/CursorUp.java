/**
 * Copyright (c) Red Hat, Inc., contributors and others 2013 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.aesh.core.internal.ansi;

import org.jboss.tools.aesh.core.document.Document;

public class CursorUp extends AbstractCommand {

	private int amount = 1;

	public CursorUp(String arguments) {
		if (!"".equals(arguments)) {
			amount = Integer.parseInt(arguments);
		}
	}

	@Override
	public CommandType getType() {
		return CommandType.CURSOR_UP;
	}

	@Override
	public void handle(Document document) {
		int currentOffset = document.getCursorOffset();
		int currentLine = document.getLineOfOffset(currentOffset);
		int newLine = currentLine - amount;
		if (newLine >= 0) {
			int currentColumn = currentOffset - document.getLineOffset(currentLine);
			int newOffset = document.getLineOffset(newLine) + currentColumn;
			document.moveCursorTo(newOffset);
		}
	}

}
