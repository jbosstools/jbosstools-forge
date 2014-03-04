package org.jboss.tools.aesh.core.internal.ansi;

import org.jboss.tools.aesh.core.ansi.AnsiControlSequence;

public interface AnsiControlSequenceFactory {

	AnsiControlSequence create(String controlSequence);
	
}
