package org.jboss.tools.aesh.core.internal.ansi;

import org.jboss.tools.aesh.core.ansi.Document;

public abstract class AbstractControlSequence implements Command {
	
	public abstract ControlSequenceType getType();
	
	public void handle(Document document) {
		throw new RuntimeException("not implemented!");
	}

}
