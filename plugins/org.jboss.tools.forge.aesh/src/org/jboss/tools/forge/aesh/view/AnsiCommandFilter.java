package org.jboss.tools.forge.aesh.view;

import org.jboss.tools.forge.aesh.view.AeshOutputStream.StreamListener;

public abstract class AnsiCommandFilter implements StreamListener {

	private StreamListener target = null;
	private StringBuffer escapeSequence = new StringBuffer();
	
	public AnsiCommandFilter(StreamListener target) {
		this.target = target;
	}
	
	public abstract void ansiCommandAvailable(String command);
	
	@Override
	public void charAppended(char c) {
		if (c == 27 && escapeSequence.length() == 0) {
			escapeSequence.append(c);
		} else if (c == '[' && escapeSequence.length() == 1) {
			escapeSequence.append(c);
		} else if (escapeSequence.length() > 1) {
			escapeSequence.append(c);
			if (isAnsiEnd(c)) {
				ansiCommandAvailable(escapeSequence.toString());
				escapeSequence.setLength(0);
			} 
		} else {
			target.charAppended(c);
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
