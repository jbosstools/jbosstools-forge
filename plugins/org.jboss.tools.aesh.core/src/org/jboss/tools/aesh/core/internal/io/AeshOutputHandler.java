package org.jboss.tools.aesh.core.internal.io;

import org.jboss.tools.aesh.core.internal.ansi.Command;

public interface AeshOutputHandler {
	
	void handleOutput(String output);
	void handleCommand(Command command);

}
