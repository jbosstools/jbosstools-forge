package org.jboss.tools.aesh.core.internal.ansi;

import org.jboss.tools.aesh.core.ansi.ControlSequence;

public interface AnsiControlSequenceFactory {

	ControlSequence create(String controlSequence);
	
}
