package org.jboss.tools.aesh.core.ansi;

import org.jboss.tools.aesh.core.document.DocumentProxy;

public abstract class ControlSequence {
	
	private String controlSequenceString;
	
	protected ControlSequence(String controlSequenceString) {
		this.controlSequenceString = controlSequenceString;
	}
	
	public String getControlSequenceString() {
		return controlSequenceString;
	}
	
	public abstract ControlSequenceType getType();
	
	public void handle(DocumentProxy document) {
		throw new RuntimeException("not implemented!");
	}

}
