/**
 * Copyright (c) Red Hat, Inc., contributors and others 2004 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.aesh.core.internal.io;

import org.jboss.tools.aesh.core.document.Document;
import org.jboss.tools.aesh.core.document.Style;
import org.jboss.tools.aesh.core.internal.ansi.Command;

public class DocumentHandler implements AeshOutputHandler {
	
	private Document document;

	@Override
	public void handleOutput(String output) {
		if ("".equals(output)) return;
		if (document != null) {
			output.replaceAll("\r", "");
			Style style = document.getCurrentStyle();
			if (style != null) {
				int increase = 
						document.getCursorOffset() - 
						document.getLength() + 
						output.length();
				style.setLength(style.getLength() + increase);
			}
			document.replace(
					document.getCursorOffset(), 
					document.getLength() - document.getCursorOffset(), 
					output);
			document.moveCursorTo(document.getCursorOffset() + output.length());
		}
	}

	@Override
	public void handleCommand(Command command) {
		if (document != null) {
			command.handle(document);
		}
	}
	
	public void setDocument(Document document) {
		this.document = document;
	}

}
