package org.jboss.tools.aesh.core.internal.io;

import org.jboss.tools.aesh.core.internal.ansi.Command;
import org.jboss.tools.aesh.core.internal.ansi.CommandFactory;
import org.jboss.tools.aesh.core.internal.ansi.DefaultCommandFactory;

public class CommandFilter implements AeshOutputFilter {
	
	private static final String ESCAPE_SEQUENCE = new String(new byte[] { 27, '[' });

	private StringBuffer escapeSequence = new StringBuffer();
	private StringBuffer targetBuffer = new StringBuffer();
	
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
	
	public void filterOutput2(String output) {
		for (int i = 0; i < output.length(); i++) {
			charAppended(output.charAt(i));
		}
		if (targetBuffer.length() > 0) {
			String targetString = targetBuffer.toString();
			targetBuffer.setLength(0);
			handler.handleOutput(targetString);
		}
	}
	
	private void charAppended(char c) {
		if (c == 27 && escapeSequence.length() == 0) {
			if (targetBuffer.length() > 0) {
				String targetString = targetBuffer.toString();
				targetBuffer.setLength(0);
				handler.handleOutput(targetString);
			}
			escapeSequence.append(c);
		} else if (c == '[' && escapeSequence.length() == 1) {
			escapeSequence.append(c);
		} else if (escapeSequence.length() > 1) {
			escapeSequence.append(c);
			Command controlSequence = commandFactory.create(escapeSequence.toString());
			if (controlSequence != null) {
				escapeSequence.setLength(0);
				handler.handleCommand(controlSequence);
			}
		} else {
			targetBuffer.append(c);
		}
	}
	
	void setCommandFactory(CommandFactory commandFactory) {
		this.commandFactory = commandFactory;
	}
	
	CommandFactory getCommandFactory() {
		return commandFactory;
	}
	
}
