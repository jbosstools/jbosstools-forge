package org.jboss.tools.aesh.core.ansi;

import org.jboss.tools.aesh.core.io.StreamListener;


public interface AnsiControlSequenceFilter extends StreamListener {

	void controlSequenceAvailable(AnsiControlSequence controlSequence);
	
}
