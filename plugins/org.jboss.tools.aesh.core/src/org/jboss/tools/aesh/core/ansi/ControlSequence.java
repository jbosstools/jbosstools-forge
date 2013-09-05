package org.jboss.tools.aesh.core.ansi;

public abstract class ControlSequence {
	
	private String controlSequenceString;
	
	protected ControlSequence(String controlSequenceString) {
		this.controlSequenceString = controlSequenceString;
	}
	
	protected String getControlSequenceString() {
		return controlSequenceString;
	}
	
	protected abstract ControlSequenceType getType();

}
