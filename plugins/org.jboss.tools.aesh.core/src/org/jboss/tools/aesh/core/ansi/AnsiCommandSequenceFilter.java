package org.jboss.tools.aesh.core.ansi;

import org.jboss.tools.aesh.core.io.AeshOutputStream.StreamListener;

public abstract class AnsiCommandSequenceFilter implements StreamListener {

	private StreamListener target = null;
	private StringBuffer escapeSequence = new StringBuffer();
	private StringBuffer targetBuffer = new StringBuffer();
	
	public AnsiCommandSequenceFilter(StreamListener target) {
		this.target = target;
	}
	
//	public abstract void executeAnsiControlSequence(AnsiControlSequence ansiControlSequence);
	
	public abstract void ansiCommandSequenceAvailable(String commandSequence);	
	
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
//			AnsiControlSequenceType ansiControlSequenceType = 
//					AnsiControlSequenceType.fromCharacter(c);
//			if (ansiControlSequenceType != null) {
//				AnsiControlSequence ansiControlSequence = 
//						AnsiControlSequenceFactory.create(
//								ansiControlSequenceType,
//								escapeSequence.toString());
//				executeAnsiControlSequence(ansiControlSequence);
//			}
			if (isAnsiEnd(c)) {
				String escapeString = escapeSequence.toString();
				escapeSequence.setLength(0);
				ansiCommandSequenceAvailable(escapeString);
			} 
		} else {
			targetBuffer.append(c);
		}
	}

	private boolean isAnsiEnd(char c) {
		return  c == 'G' || 
				c == 'K' ||
				c == 'm' ||
				c == 'H' ||
				c == 'J';
	}
	
}
