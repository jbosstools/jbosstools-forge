package org.jboss.tools.aesh.core.ansi;

import org.jboss.tools.aesh.core.document.DocumentProxy;

public abstract class ControlSequence {
	
	public abstract ControlSequenceType getType();
	
	public void handle(DocumentProxy document) {
		throw new RuntimeException("not implemented!");
	}

}
