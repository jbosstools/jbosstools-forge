package org.jboss.tools.aesh.core.console;

import java.io.InputStream;
import java.io.OutputStream;

import org.jboss.tools.aesh.core.ansi.Document;
import org.jboss.tools.aesh.core.internal.io.AeshInputStream;
import org.jboss.tools.aesh.core.internal.io.AeshOutputStream;
import org.jboss.tools.aesh.core.internal.io.DocumentHandler;

public abstract class AbstractConsole implements Console {
	
	private AeshInputStream inputStream = null;
	private AeshOutputStream outputStream, errorStream = null;
	private DocumentHandler handler = new DocumentHandler();
	
	public AbstractConsole() {
		initialize();
	}

	public abstract void start();
	public abstract void stop();
	protected abstract void createConsole();

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

	
	protected void initialize() {
		createStreams();
	}
	
	public void createStreams() {
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
		return new AeshOutputStream(handler);
	}
	
	private AeshOutputStream createErrorStream() {
		return new AeshOutputStream(handler);
	}
	
}
