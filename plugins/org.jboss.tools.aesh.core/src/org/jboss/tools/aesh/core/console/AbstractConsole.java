package org.jboss.tools.aesh.core.console;

import java.io.InputStream;
import java.io.OutputStream;

import org.jboss.tools.aesh.core.document.Document;
import org.jboss.tools.aesh.core.internal.io.AeshInputStream;
import org.jboss.tools.aesh.core.internal.io.AeshOutputFilter;
import org.jboss.tools.aesh.core.internal.io.AeshOutputStream;
import org.jboss.tools.aesh.core.internal.io.CommandFilter;
import org.jboss.tools.aesh.core.internal.io.DocumentHandler;

public abstract class AbstractConsole implements Console {
	
	private AeshInputStream inputStream = null;
	private AeshOutputStream outputStream, errorStream = null;
	private DocumentHandler handler = null;
	private AeshOutputFilter filter = null;
	
	public AbstractConsole() {
		handler = new DocumentHandler();
		filter = new CommandFilter(handler);
		initialize();
	}

	public abstract void start();
	public abstract void stop();

	public void connect(Document document) {
		handler.setDocument(document);		
	}
	
	public void disconnect() {
		handler.setDocument(null);
	}
	
	public void sendInput(String input) {
		if (inputStream != null) {
			inputStream.append(input);
		}
	}

	public void initialize() {
		createStreams();
	}
	
	protected void createStreams() {
		inputStream = createInputStream();
		outputStream = createOutputStream();
		errorStream = createErrorStream();
	}

	protected InputStream getInputStream() {
		return inputStream;
	}

	protected OutputStream getOutputStream() {
		return outputStream;
	}

	protected OutputStream getErrorStream() {
		return errorStream;
	}
	
	private AeshInputStream createInputStream() {
		return new AeshInputStream(); 
	}
	
	private AeshOutputStream createOutputStream() {
		return new AeshOutputStream(filter);
	}
	
	private AeshOutputStream createErrorStream() {
		return new AeshOutputStream(filter);
	}
	
}
