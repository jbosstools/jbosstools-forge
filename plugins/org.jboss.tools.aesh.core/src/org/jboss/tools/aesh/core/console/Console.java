package org.jboss.tools.aesh.core.console;

import org.jboss.tools.aesh.core.ansi.Document;

public interface Console {

	void start();
	void sendInput(String input);
	void stop();
	
	void connect(Document document);
	void disconnect();
	
}
