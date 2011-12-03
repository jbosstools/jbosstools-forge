package org.jboss.tools.forge.ui.console;

import java.util.Map;

public interface ForgeCommandPostProcessor {
	
	void postProcess(Map<String, String> commandDetails);

}
