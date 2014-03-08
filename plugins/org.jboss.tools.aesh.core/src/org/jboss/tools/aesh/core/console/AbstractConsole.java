package org.jboss.tools.aesh.core.console;

import java.io.InputStream;
import java.io.OutputStream;

import org.jboss.tools.aesh.core.ansi.ControlSequence;
import org.jboss.tools.aesh.core.ansi.Document;
import org.jboss.tools.aesh.core.ansi.StyleRange;
import org.jboss.tools.aesh.core.internal.io.AeshInputStream;
import org.jboss.tools.aesh.core.internal.io.ControlSequenceOutputStream;

public abstract class AbstractConsole implements AeshConsole {
	
	private AeshInputStream inputStream = null;
	private OutputStream outputStream, errorStream = null;
	private Document document = null;
	
	public AbstractConsole() {
		initialize();
	}

	public abstract void start();
	public abstract void stop();
	protected abstract void createConsole();

	public void connect(Document document) {
		if (this.document != null) {
			disconnect();
		}
		this.document = document;
	}
	
	public void disconnect() {
		document = null;
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
	
	private OutputStream createOutputStream() {
		return new ControlSequenceOutputStream() {			
			@Override
			public void onOutput(String string) {
				handleOutput(string);
			}			
			@Override
			public void onControlSequence(ControlSequence controlSequence) {
				handleControlSequence(controlSequence);
			}
		};
	}
	
	private OutputStream createErrorStream() {
		return new ControlSequenceOutputStream() {			
			@Override
			public void onOutput(String string) {
				handleOutput(string);
			}			
			@Override
			public void onControlSequence(ControlSequence controlSequence) {
				handleControlSequence(controlSequence);
			}
		};
	}
	
	private void handleControlSequence(ControlSequence controlSequence) {
		if (document != null) {
			controlSequence.handle(document);
		}
	}
	
	private void handleOutput(String string) {
		if (document != null) {
			string.replaceAll("\r", "");
			StyleRange style = document.getCurrentStyleRange();
			if (style != null) {
				int increase = 
						document.getCursorOffset() - 
						document.getLength() + 
						string.length();
				style.setLength(style.getLength() + increase);
			}
			document.replace(
					document.getCursorOffset(), 
					document.getLength() - document.getCursorOffset(), 
					string);
			document.moveCursorTo(document.getCursorOffset() + string.length());
		}
	}

}
