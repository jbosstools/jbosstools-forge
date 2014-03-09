package org.jboss.tools.aesh.core.internal.ansi;

import org.jboss.tools.aesh.core.ansi.Document;

public abstract class AbstractCommand implements Command {
	
	public abstract ControlSequenceType getType();
	
	public void handle(Document document) {
		throw new RuntimeException("not implemented!");
	}

}
