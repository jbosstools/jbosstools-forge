package org.jboss.tools.aesh.core.ansi;

import org.jboss.tools.aesh.core.document.AeshDocument;

public interface AnsiControlSequence {
	
	void handle(AeshDocument document);

}
