package org.jboss.tools.forge.ui.internal.commands;

import java.util.Map;

public interface ForgeCommandPostProcessor {
	
	void postProcess(Map<String, String> commandDetails);

}
