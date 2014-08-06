/**
 * Copyright (c) Red Hat, Inc., contributors and others 2013 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.aesh.core.console;

import org.jboss.tools.aesh.core.document.Document;

public interface Console {

	void start();
	void sendInput(String input);
	void stop();
	
	void connect(Document document);
	void disconnect();
	
	Object getCurrentResource();
}
