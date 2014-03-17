package org.jboss.tools.aesh.core.console;

import org.jboss.tools.aesh.core.document.Document;

public interface Console {

	void initialize();
	void start();
	void sendInput(String input);
	void stop();
	
	void connect(Document document);
	void disconnect();
	
}
