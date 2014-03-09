package org.jboss.tools.aesh.core.internal.ansi;


public interface CommandFactory {

	Command create(String controlSequence);
	
}
