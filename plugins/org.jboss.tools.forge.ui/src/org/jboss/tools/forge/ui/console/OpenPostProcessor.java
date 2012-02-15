package org.jboss.tools.forge.ui.console;

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
