package org.jboss.tools.aesh.core.internal.io;

import org.jboss.tools.aesh.core.internal.ansi.Command;

public interface Handler {
	
	void handleOutput(String output);
	void handleCommand(Command command);

}
