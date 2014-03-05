package org.jboss.tools.aesh.core.internal.ansi;

import org.jboss.tools.aesh.core.ansi.AnsiDocument;
import org.jboss.tools.aesh.core.ansi.ControlSequence;

public abstract class AbstractAnsiControlSequence implements ControlSequence {
	
	public abstract AnsiControlSequenceType getType();
	
	public void handle(AnsiDocument document) {
		throw new RuntimeException("not implemented!");
	}

}
