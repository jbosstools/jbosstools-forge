package org.jboss.tools.aesh.core.ansi;

import org.jboss.tools.aesh.core.internal.ansi.AnsiControlSequenceFactory;
import org.jboss.tools.aesh.core.internal.ansi.DefaultControlSequenceFactory;
import org.jboss.tools.aesh.core.io.StreamListener;

public class AnsiControlSequenceFilter implements StreamListener {

	private StreamListener target = null;
	private StringBuffer escapeSequence = new StringBuffer();
	private StringBuffer targetBuffer = new StringBuffer();
	
	private AnsiControlSequenceFactory controlSequenceFactory = DefaultControlSequenceFactory.INSTANCE;
	private AnsiControlSequenceHandler controlSequenceHandler;
	
	public AnsiControlSequenceFilter(StreamListener target, AnsiControlSequenceHandler handler) {
		this.target = target;
		this.controlSequenceHandler = handler;
	}
	
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
			ControlSequence controlSequence = controlSequenceFactory.create(escapeSequence.toString());
			if (controlSequence != null) {
				escapeSequence.setLength(0);
				controlSequenceHandler.handle(controlSequence);;
			}
		} else {
			targetBuffer.append(c);
		}
	}
	
	void setControlSequenceFactory(AnsiControlSequenceFactory controlSequenceFactory) {
		this.controlSequenceFactory = controlSequenceFactory;
	}
	
	AnsiControlSequenceFactory getControlSequenceFactory() {
		return controlSequenceFactory;
	}
	
}
