package org.jboss.tools.aesh.core.ansi;

import org.jboss.tools.aesh.core.document.AeshDocument;

public abstract class ControlSequence {
	
	public abstract ControlSequenceType getType();
	
	public void handle(AeshDocument document) {
		throw new RuntimeException("not implemented!");
	}

}
