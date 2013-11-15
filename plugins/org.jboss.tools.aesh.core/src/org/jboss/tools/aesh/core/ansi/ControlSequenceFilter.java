package org.jboss.tools.aesh.core.ansi;

import org.jboss.tools.aesh.core.io.AeshOutputStream.StreamListener;

public abstract class ControlSequenceFilter implements StreamListener {

	private StreamListener target = null;
	private StringBuffer escapeSequence = new StringBuffer();
	private StringBuffer targetBuffer = new StringBuffer();
	
	public ControlSequenceFilter(StreamListener target) {
		this.target = target;
	}
	
	public abstract void controlSequenceAvailable(ControlSequence controlSequence);	
	
	@Override
	public void outputAvailable(String output) {
		for (int i = 0; i < output.length(); i++) {
			charAppended(output.charAt(i));
		}
		if (targetBuffer.length() > 0) {
			String targetString = targetBuffer.toString();
			targetBuffer.setLength(0);
			target.outputAvailable(targetString);
		}
	}
	
	private void charAppended(char c) {
		if (c == 27 && escapeSequence.length() == 0) {
			if (targetBuffer.length() > 0) {
				String targetString = targetBuffer.toString();
				targetBuffer.setLength(0);
				target.outputAvailable(targetString);
			}
			escapeSequence.append(c);
		} else if (c == '[' && escapeSequence.length() == 1) {
			escapeSequence.append(c);
		} else if (escapeSequence.length() > 1) {
			escapeSequence.append(c);
			ControlSequence command = ControlSequenceFactory.create(escapeSequence.toString());
			if (command != null) {
				escapeSequence.setLength(0);
				controlSequenceAvailable(command);
			}
		} else {
			targetBuffer.append(c);
		}
	}

}
