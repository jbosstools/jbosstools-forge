/**
 * Copyright (c) Red Hat, Inc., contributors and others 2013 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.forge.ui.internal.commands;

import java.util.Map;

public class OpenPostProcessor extends PickUpPostProcessor {
	
	protected String getResourceToOpen(Map<String, String> commandDetails) {
		String par = commandDetails.get("par");
		if (par.startsWith("~")) {
			return System.getProperty("user.home") + par.substring(1);
		} else if (!par.startsWith("/")) {
			String crn = commandDetails.get("crn");
			return crn + "/" + par;
		} else {
			return par;
		}
	}
	
}
