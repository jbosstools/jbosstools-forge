/**
 * Copyright (c) Red Hat, Inc., contributors and others 2013 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.aesh.core.internal.ansi;

import org.jboss.tools.aesh.core.document.Document;
import org.jboss.tools.aesh.core.internal.AeshCorePlugin;

public class CursorHorizontalAbsolute extends AbstractCommand {

	private int column = 1;

	public CursorHorizontalAbsolute(String arguments) {
		try {
			column = Math.max(column, Integer.parseInt(arguments));
		} catch (NumberFormatException e) {
			AeshCorePlugin.log(e);
		}
	}

	@Override
	public CommandType getType() {
		return CommandType.CURSOR_HORIZONTAL_ABSOLUTE;
	}

	@Override
	public void handle(Document document) {
		int lineStart = document.getLineOffset(document.getLineOfOffset(document.getCursorOffset()));
		document.moveCursorTo(lineStart + column - 1);
	}

}
