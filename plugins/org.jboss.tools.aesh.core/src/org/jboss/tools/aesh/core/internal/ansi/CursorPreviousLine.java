/**
 * Copyright (c) Red Hat, Inc., contributors and others 2004 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.aesh.core.internal.ansi;



public class CursorPreviousLine extends AbstractCommand {

	public CursorPreviousLine(String arguments) {}

	@Override
	public CommandType getType() {
		return CommandType.CURSOR_PREVIOUS_LINE;
	}

}
