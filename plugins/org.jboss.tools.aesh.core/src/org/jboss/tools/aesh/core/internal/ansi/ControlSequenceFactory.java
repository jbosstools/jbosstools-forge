package org.jboss.tools.aesh.core.internal.ansi;

import org.jboss.tools.aesh.core.ansi.Command;

public interface ControlSequenceFactory {

	Command create(String controlSequence);
	
}
