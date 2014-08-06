/**
 * Copyright (c) Red Hat, Inc., contributors and others 2004 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.aesh.core.internal.ansi;

import org.jboss.tools.aesh.core.document.Document;


public class CursorPosition extends AbstractCommand {
	
	private int line, column = 0;

	// need to subtract 1 from the arguments as the ansi cursor position arguments are 1-based
	public CursorPosition(String arguments) {
    	int i = arguments.indexOf(';');
    	if (i != -1) {
    		line = Integer.valueOf(arguments.substring(0, i)) - 1;
    		column = Integer.valueOf(arguments.substring(i + 1)) - 1;
    	} else if (arguments.length() > 0) {
    		line = Integer.valueOf(arguments) - 1;
    	}
	}

	@Override
	public CommandType getType() {
		return CommandType.CURSOR_POSITION;
	}
	
	@Override
	public void handle(Document document) {
    	int offset = document.getLineOffset(line);
    	int maxColumn = document.getLineLength(line);
    	offset += Math.min(maxColumn, column);
    	document.moveCursorTo(offset);
	}

}
