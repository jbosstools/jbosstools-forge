package org.jboss.tools.forge.core.io;


public abstract class ForgeAnsiCommandFilter implements ForgeOutputListener {
	
	private ForgeOutputListener target = null;
	private StringBuffer escapeSequence = new StringBuffer();
	private StringBuffer targetBuffer = new StringBuffer();
	
	public ForgeAnsiCommandFilter(ForgeOutputListener target) {
		this.target = target;
	}
	
	public abstract void ansiCommandAvailable(String command);

	@Override
	public void outputAvailable(String output) {
		for (int i = 0; i < output.length(); i++) {
			char c = output.charAt(i);
			if (c == 27) {
				if (escapeSequence.length() == 0) {
					if (targetBuffer.length() > 0) {
						target.outputAvailable(targetBuffer.toString());
						targetBuffer.setLength(0);
					}
					escapeSequence.append(c);
				}
			} else if (c == '[') {
				if (escapeSequence.length() == 1) {
					escapeSequence.append(c);
				} else {
					targetBuffer.append(c);
				}
			} else if (escapeSequence.length() > 1) {
				escapeSequence.append(c);
				if (isAnsiEnd(c)) {
					ansiCommandAvailable(escapeSequence.toString());
					escapeSequence.setLength(0);
				} 
			} else {
				targetBuffer.append(c);
			}
		}
		if (targetBuffer.length() > 0) {
			target.outputAvailable(targetBuffer.toString());
			targetBuffer.setLength(0);
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
