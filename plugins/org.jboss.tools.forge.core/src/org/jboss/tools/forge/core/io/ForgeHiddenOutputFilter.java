package org.jboss.tools.forge.core.io;

public class ForgeHiddenOutputFilter implements ForgeOutputListener {
	
	private ForgeOutputListener target = null;
	private boolean hidden = false;
	private StringBuffer hiddenBuffer = new StringBuffer();
	private StringBuffer targetBuffer = new StringBuffer();	
	private StringBuffer escapeSequence = new StringBuffer(); 
	
	public ForgeHiddenOutputFilter(ForgeOutputListener target) {
		this.target = target;
	}
	
	@Override
	public void outputAvailable(String output) {
		for (int i = 0; i < output.length(); i++) {
			char c = output.charAt(i);
			if (c == 27) {
				if (escapeSequence.length() == 0) {
					escapeSequence.append(c);
				} else {
					escapeSequence.append(c);
					if (hidden) {
						hiddenBuffer.append(escapeSequence);
					} else {
						targetBuffer.append(escapeSequence);
					}
					escapeSequence.setLength(0);
				}
			} else if (c == '[') {
				if (escapeSequence.length() == 1) {
					escapeSequence.append(c);
				} else {
					escapeSequence.append(c);
					if (hidden) {
						hiddenBuffer.append(escapeSequence);
					} else {
						targetBuffer.append(escapeSequence);
					}
					escapeSequence.setLength(0);
				}
			} else if (c == '%') {
				if (escapeSequence.length() == 2) {
					if (hidden) {
						handleHiddenOutput(hiddenBuffer);
						hiddenBuffer.setLength(0);
					} else {
						String out = targetBuffer.toString();
						targetBuffer.setLength(0);
						target.outputAvailable(out);
					}
					escapeSequence.setLength(0);
					hidden = !hidden;
				} else {
					escapeSequence.append(c);
					if (hidden) {
//						appendToHiddenBuffer(escapeSequence);
						hiddenBuffer.append(escapeSequence);
					} else {
//						appendToTargetBuffer(escapeSequence);
						targetBuffer.append(escapeSequence);
					}
					escapeSequence.setLength(0);
				}
			} else {
				escapeSequence.append(c);
				if (hidden) {
//					appendToHiddenBuffer(escapeSequence);
					hiddenBuffer.append(escapeSequence);
				} else {
//					appendToTargetBuffer(escapeSequence);
					targetBuffer.append(escapeSequence);
				}
				escapeSequence.setLength(0);
			}
		}
		if (hiddenBuffer.length() == 0 && targetBuffer.length() != 0) {
			String out = targetBuffer.toString();
			targetBuffer.setLength(0);
			target.outputAvailable(out);
		}
		
	}
	
	private void handleHiddenOutput(StringBuffer output) {
		System.out.println("handleHiddenOutput : " + output.toString());
	}
	
	private void appendToTargetBuffer(StringBuffer buffer) {
		System.out.println("appendToTargetBuffer : " + buffer);
		targetBuffer.append(buffer);
	}
	
	private void appendToHiddenBuffer(StringBuffer buffer) {
		System.out.println("appendToHiddenBuffer : " + buffer);
	}

}
