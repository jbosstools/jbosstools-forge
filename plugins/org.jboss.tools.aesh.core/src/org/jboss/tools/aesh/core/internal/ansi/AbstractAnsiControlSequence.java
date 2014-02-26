package org.jboss.tools.aesh.core.internal.ansi;

import org.jboss.tools.aesh.core.ansi.AnsiControlSequence;
import org.jboss.tools.aesh.core.document.AeshDocument;

public abstract class AbstractAnsiControlSequence implements AnsiControlSequence {
	
	public abstract AnsiControlSequenceType getType();
	
	public void handle(AeshDocument document) {
		throw new RuntimeException("not implemented!");
	}

}
