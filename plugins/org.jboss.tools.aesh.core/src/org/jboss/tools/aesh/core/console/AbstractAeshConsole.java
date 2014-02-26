package org.jboss.tools.aesh.core.console;

import java.io.InputStream;
import java.io.OutputStream;

import org.jboss.tools.aesh.core.internal.io.AeshInputStream;
import org.jboss.tools.aesh.core.internal.io.AeshOutputStream;
import org.jboss.tools.aesh.core.io.StreamListener;

public abstract class AbstractAeshConsole implements AeshConsole {

	private AeshInputStream inputStream;
	private AeshOutputStream stdOut, stdErr;

	public AbstractAeshConsole() {
		initialize();
	}

	protected void initialize() {
		createStreams();
		createConsole();
	}
	
	protected abstract void createConsole();

	protected void createStreams() {
		inputStream = new AeshInputStream();
		stdOut = new AeshOutputStream();
		stdErr = new AeshOutputStream();
	}

	public void sendInput(String input) {
		inputStream.append(input);
	}

	public void addStdOutListener(StreamListener listener) {
		stdOut.addStreamListener(listener);
	}

	public void removeStdOutListener(StreamListener listener) {
		stdOut.removeStreamListener(listener);
	}

	public void addStdErrListener(StreamListener listener) {
		stdErr.addStreamListener(listener);
	}

	public void removeStdErrListener(StreamListener listener) {
		stdErr.removeStreamListener(listener);
	}

	protected InputStream getInputStream() {
		return inputStream;
	}

	protected OutputStream getStdOut() {
		return stdOut;
	}

	protected OutputStream getStdErr() {
		return stdErr;
	}

}
