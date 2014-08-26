/**
 * Copyright (c) Red Hat, Inc., contributors and others 2013 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.aesh.core.internal.ansi;

import org.jboss.tools.aesh.core.document.Document;
import org.jboss.tools.aesh.core.internal.AeshCorePlugin;
import org.jboss.tools.aesh.core.internal.io.AeshInputStream;

public abstract class AbstractCommand implements Command {

	public abstract CommandType getType();

	public void handle(Document document) {
		AeshCorePlugin.log(new Throwable("Unimplemented command: " + getType()));
	}
	
	public void handle(AeshInputStream inputStream, Document document) {
		handle(document);
	}

}
