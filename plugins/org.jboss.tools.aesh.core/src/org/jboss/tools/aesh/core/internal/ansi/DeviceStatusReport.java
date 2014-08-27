/**
 * Copyright (c) Red Hat, Inc., contributors and others 2013 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.aesh.core.internal.ansi;

import org.jboss.aesh.util.ANSI;
import org.jboss.tools.aesh.core.document.Document;
import org.jboss.tools.aesh.core.internal.io.AeshInputStream;

public class DeviceStatusReport extends AbstractCommand {

	private static char SEMI_COLON = ';';

	public DeviceStatusReport(String arguments) {}

	@Override
	public CommandType getType() {
		return CommandType.DEVICE_STATUS_REPORT;
	}

	@Override
	public void handle(AeshInputStream inputStream, Document document) {
		int row = document.getLineOfOffset(document.getCursorOffset());
		int column = document.getLineOffset(row);
		String reportCursorPosition = ANSI.getStart() + Integer.toString(row)
				+ SEMI_COLON + Integer.toString(column) + 'R';
		// Respond to the Device Status Report with a Cursor Position Report
		inputStream.append(reportCursorPosition);
	}

}
