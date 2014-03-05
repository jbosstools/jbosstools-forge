package org.jboss.tools.aesh.core.internal.ansi;

import org.jboss.tools.aesh.core.ansi.Document;
import org.jboss.tools.aesh.core.ansi.ControlSequence;

public abstract class AbstractControlSequence implements ControlSequence {
	
	public abstract ControlSequenceType getType();
	
	public void handle(Document document) {
		throw new RuntimeException("not implemented!");
	}

}
