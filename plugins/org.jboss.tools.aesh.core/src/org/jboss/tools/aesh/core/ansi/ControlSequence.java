package org.jboss.tools.aesh.core.ansi;

public abstract class ControlSequence {
	
	private String controlSequenceString;
	
	protected ControlSequence(String controlSequenceString) {
		this.controlSequenceString = controlSequenceString;
	}
	
	public String getControlSequenceString() {
		return controlSequenceString;
	}
	
	public abstract ControlSequenceType getType();

}
