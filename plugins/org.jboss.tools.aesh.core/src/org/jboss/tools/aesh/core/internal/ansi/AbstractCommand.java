package org.jboss.tools.aesh.core.internal.ansi;

import org.jboss.tools.aesh.core.document.Document;
import org.jboss.tools.aesh.core.internal.AeshCorePlugin;

public abstract class AbstractCommand implements Command {

	public abstract CommandType getType();

	public void handle(Document document) {
		AeshCorePlugin.log(new Throwable("Unimplemented command: " + getType()));
	}

}
