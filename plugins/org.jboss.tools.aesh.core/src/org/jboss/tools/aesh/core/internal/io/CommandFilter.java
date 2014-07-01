package org.jboss.tools.aesh.core.internal.io;

import org.jboss.tools.aesh.core.internal.ansi.Command;
import org.jboss.tools.aesh.core.internal.ansi.CommandFactory;
import org.jboss.tools.aesh.core.internal.ansi.DefaultCommandFactory;

public class CommandFilter implements AeshOutputFilter {
	
	private static final String ESCAPE_SEQUENCE = new String(new byte[] { 27, '[' });

	private CommandFactory commandFactory = DefaultCommandFactory.INSTANCE;
	
	private AeshOutputHandler handler = null;
	
	public CommandFilter(AeshOutputHandler handler) {
		this.handler = handler;
	}
	
	public void filterOutput(String output) {
		int index = 0;
		while (true) {
			int controlSequenceStart = output.indexOf(ESCAPE_SEQUENCE, index);
			if (controlSequenceStart == -1) break;
			handler.handleOutput(output.substring(index, controlSequenceStart));
			int controlSequenceEnd = controlSequenceStart + 3;
			while (true) {
				Command command = 
						commandFactory.create(
								output.substring(controlSequenceStart, controlSequenceEnd));
				if (command != null) {
					handler.handleCommand(command);
					index = controlSequenceEnd;
					break;
				} else {
					controlSequenceEnd++;
				}
			}
		}
		handler.handleOutput(output.substring(index));
	}
	
	void setCommandFactory(CommandFactory commandFactory) {
		this.commandFactory = commandFactory;
	}
	
	CommandFactory getCommandFactory() {
		return commandFactory;
	}
	
}
