/**
 * Copyright (c) Red Hat, Inc., contributors and others 2013 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.forge.core.io;

public abstract class ForgeHiddenOutputFilter implements ForgeOutputFilter {
	
	private ForgeOutputListener target = null;
	private boolean hidden = false;
	private StringBuffer hiddenBuffer = new StringBuffer();
	private StringBuffer targetBuffer = new StringBuffer();	
	private StringBuffer escapeSequence = new StringBuffer(); 
	
	public ForgeHiddenOutputFilter() {}
	
	public ForgeHiddenOutputFilter(ForgeOutputListener target) {
		this.target = target;
	}
	
	protected ForgeOutputListener getTarget() {
		return target;
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
			} else if (c == '%') {
				if (escapeSequence.length() == 1) {
					if (hidden) {
						String toHandle = hiddenBuffer.toString();
						hiddenBuffer.setLength(0);
						handleFilteredString(toHandle);
					} else {
						String out = targetBuffer.toString();
						targetBuffer.setLength(0);
						if (target != null) {
							target.outputAvailable(out);
						}
					}
					escapeSequence.setLength(0);
					hidden = !hidden;
				} else {
					escapeSequence.append(c);
					if (hidden) {
						hiddenBuffer.append(escapeSequence);
					} else {
						targetBuffer.append(escapeSequence);
					}
					escapeSequence.setLength(0);
				}
			} else {
				escapeSequence.append(c);
				if (hidden) {
					hiddenBuffer.append(escapeSequence);
				} else {
					targetBuffer.append(escapeSequence);
				}
				escapeSequence.setLength(0);
			}
		}
		if (hiddenBuffer.length() == 0 && targetBuffer.length() != 0) {
			String out = targetBuffer.toString();
			targetBuffer.setLength(0);
			if (target != null) {
				target.outputAvailable(out);
			}
		}
		
	}
	
}
