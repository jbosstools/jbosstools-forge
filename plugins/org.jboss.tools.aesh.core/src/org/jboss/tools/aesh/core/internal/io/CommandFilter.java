package org.jboss.tools.aesh.core.internal.io;

import org.jboss.tools.aesh.core.internal.ansi.Command;
import org.jboss.tools.aesh.core.internal.ansi.CommandFactory;
import org.jboss.tools.aesh.core.internal.ansi.DefaultCommandFactory;

public class CommandFilter implements AeshOutputFilter {
	
	private static final char ESCAPE_CHAR = 27;
	private static final char CURSOR_SAVE = '7';
	private static final char CURSOR_RESTORE = '8';
	private static final char LEFT_BRACKET = '[';
	private static final String CURSOR_SAVE_SEQUENCE = new String(new char[] { ESCAPE_CHAR, LEFT_BRACKET, 's' });
	private static final String CURSOR_RESTORE_SEQUENCE = new String(new char[] { ESCAPE_CHAR, LEFT_BRACKET, 'u' });

	private CommandFactory commandFactory = DefaultCommandFactory.INSTANCE;
	
	private AeshOutputHandler handler = null;
	
	public CommandFilter(AeshOutputHandler handler) {
		this.handler = handler;
	}
	
	public void filterOutput(String output) {
		int index = 0;
		while (true) {
			int controlSequenceStart = output.indexOf(ESCAPE_CHAR, index);
			if (controlSequenceStart == -1) break;
			handler.handleOutput(output.substring(index, controlSequenceStart));
			char next = output.charAt(controlSequenceStart + 1);
			if (next == CURSOR_SAVE) {
				handler.handleCommand(commandFactory.create(CURSOR_SAVE_SEQUENCE));
				index = controlSequenceStart + 2;
			} else if (next == CURSOR_RESTORE) {
				handler.handleCommand(commandFactory.create(CURSOR_RESTORE_SEQUENCE));
				index = controlSequenceStart + 2;
			} else if (next == LEFT_BRACKET) {
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
			} else {
				throw new RuntimeException("Unknown ANSI command sequence");
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
