package org.jboss.tools.aesh.core.ansi;

import org.jboss.tools.aesh.core.document.Document;

public abstract class ControlSequence {
	
	public abstract ControlSequenceType getType();
	
	public void handle(Document document) {
		throw new RuntimeException("not implemented!");
	}

}
