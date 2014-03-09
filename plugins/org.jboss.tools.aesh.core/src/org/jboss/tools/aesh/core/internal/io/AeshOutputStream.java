package org.jboss.tools.aesh.core.internal.io;

import java.io.IOException;
import java.io.OutputStream;

import org.jboss.tools.aesh.core.internal.ansi.Command;
import org.jboss.tools.aesh.core.internal.ansi.CommandFactory;
import org.jboss.tools.aesh.core.internal.ansi.DefaultCommandFactory;

public abstract class AeshOutputStream extends OutputStream {
	
	private StringBuffer escapeSequence = new StringBuffer();
	private StringBuffer targetBuffer = new StringBuffer();
	
	private CommandFactory controlSequenceFactory = DefaultCommandFactory.INSTANCE;
	
	public abstract void onCommand(Command command);
	public abstract void onOutput(String string);

	@Override
	public void write(int i) throws IOException {
		outputAvailable(new String( new char[] { (char)i }));
	}
	
	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		outputAvailable(new String(b).substring(off, off + len));
	}
	
	private void outputAvailable(String output) throws IOException {
		for (int i = 0; i < output.length(); i++) {
			charAppended(output.charAt(i));
		}
		if (targetBuffer.length() > 0) {
			String targetString = targetBuffer.toString();
			targetBuffer.setLength(0);
			onOutput(targetString);
		}
	}
	
	private void charAppended(char c) throws IOException {
		if (c == 27 && escapeSequence.length() == 0) {
			if (targetBuffer.length() > 0) {
				String targetString = targetBuffer.toString();
				targetBuffer.setLength(0);
				onOutput(targetString);
			}
			escapeSequence.append(c);
		} else if (c == '[' && escapeSequence.length() == 1) {
			escapeSequence.append(c);
		} else if (escapeSequence.length() > 1) {
			escapeSequence.append(c);
			Command controlSequence = controlSequenceFactory.create(escapeSequence.toString());
			if (controlSequence != null) {
				escapeSequence.setLength(0);
				onCommand(controlSequence);
			}
		} else {
			targetBuffer.append(c);
		}
	}
	
	void setControlSequenceFactory(CommandFactory controlSequenceFactory) {
		this.controlSequenceFactory = controlSequenceFactory;
	}
	
	CommandFactory getControlSequenceFactory() {
		return controlSequenceFactory;
	}
	
}
