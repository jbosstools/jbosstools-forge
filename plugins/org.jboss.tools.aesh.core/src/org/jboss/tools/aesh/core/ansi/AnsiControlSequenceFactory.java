package org.jboss.tools.aesh.core.ansi;

public interface AnsiControlSequenceFactory {

	AnsiControlSequence create(String controlSequence);
	
}
