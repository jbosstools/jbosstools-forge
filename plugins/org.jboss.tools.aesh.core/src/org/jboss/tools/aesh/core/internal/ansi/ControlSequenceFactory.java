package org.jboss.tools.aesh.core.internal.ansi;

import org.jboss.tools.aesh.core.ansi.ControlSequence;

public interface ControlSequenceFactory {

	ControlSequence create(String controlSequence);
	
}
