package org.jboss.tools.aesh.core.internal.ansi;


public interface ControlSequenceFactory {

	Command create(String controlSequence);
	
}
