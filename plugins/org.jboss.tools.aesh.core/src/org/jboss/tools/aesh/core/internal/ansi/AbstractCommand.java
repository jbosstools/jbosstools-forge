package org.jboss.tools.aesh.core.internal.ansi;

import org.jboss.tools.aesh.core.document.Document;

public abstract class AbstractCommand implements Command {
	
	public final static String NOT_IMPLEMENTED = "not implemented!";
	
	public abstract CommandType getType();
	
	public void handle(Document document) {
		throw new RuntimeException(NOT_IMPLEMENTED);
	}

}
