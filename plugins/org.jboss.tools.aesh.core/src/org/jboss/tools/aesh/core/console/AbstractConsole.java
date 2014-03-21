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
	}

	public abstract void start();
	public abstract void stop();

	public void connect(Document document) {
		setInputStream(new AeshInputStream()); 
		setOutputStream( new AeshOutputStream(filter));
		setErrorStream(new AeshOutputStream(filter));
		setHandlerDocument(document);		
	}
	
	public void disconnect() {
		setHandlerDocument(null);
		setErrorStream(null);
		setOutputStream(null);
		setInputStream(null);
	}
	
	public void sendInput(String input) {
		if (inputStream != null) {
			inputStream.append(input);
		}
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
	
	void setInputStream(AeshInputStream inputStream) {
		this.inputStream = inputStream;
	}
	
	void setOutputStream(AeshOutputStream outputStream) {
		this.outputStream = outputStream;
	}
	
	void setErrorStream(AeshOutputStream errorStream) {
		this.errorStream = errorStream;
	}
	
	void setHandlerDocument(Document document) {
		handler.setDocument(document);
	}
	
}
