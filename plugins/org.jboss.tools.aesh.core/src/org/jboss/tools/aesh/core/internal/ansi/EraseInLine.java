/**
 * Copyright (c) Red Hat, Inc., contributors and others 2013 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
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
