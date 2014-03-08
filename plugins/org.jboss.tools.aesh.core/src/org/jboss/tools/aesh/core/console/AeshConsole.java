package org.jboss.tools.aesh.core.console;

import org.jboss.tools.aesh.core.ansi.Document;
import org.jboss.tools.aesh.core.io.StreamListener;

public interface AeshConsole {

	void start();
	void sendInput(String input);
	void stop();
	
	void connect(Document document);
	void disconnect();
	
	void addStdOutListener(StreamListener listener);
	void removeStdOutListener(StreamListener listener);
	void addStdErrListener(StreamListener listener);
	void removeStdErrListener(StreamListener listener);
	
}
