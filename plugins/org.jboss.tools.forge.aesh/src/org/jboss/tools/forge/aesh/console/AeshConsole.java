package org.jboss.tools.forge.aesh.console;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.jboss.aesh.console.Console;
import org.jboss.aesh.console.ConsoleCallback;
import org.jboss.aesh.console.ConsoleOutput;
import org.jboss.aesh.console.Prompt;
import org.jboss.aesh.console.settings.Settings;
import org.jboss.aesh.console.settings.SettingsBuilder;
import org.jboss.aesh.terminal.CharacterType;
import org.jboss.aesh.terminal.Color;
import org.jboss.aesh.terminal.TerminalCharacter;
import org.jboss.tools.forge.aesh.io.AeshInputStream;
import org.jboss.tools.forge.aesh.io.AeshOutputStream;
import org.jboss.tools.forge.aesh.io.AeshOutputStream.StreamListener;

public class AeshConsole {
	
	private AeshInputStream inputStream;
	private AeshOutputStream stdOut, stdErr;
	private Console console;
	
	public AeshConsole() {
		initialize();
	}
	
	protected void initialize() {
		createStreams();
		createConsole();
	}
	
	protected void createConsole() {
		try {
			console = new Console(createAeshSettings());
			console.setPrompt(createPrompt());
			console.setConsoleCallback(createConsoleCallback());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	protected Settings createAeshSettings() {
		return new SettingsBuilder()
			.inputStream(inputStream)
			.outputStream(stdOut)
			.outputStreamError(stdErr)
			.create();
	}
	
	protected void createStreams() {
		inputStream = new AeshInputStream();
		stdOut = new AeshOutputStream();
		stdErr = new AeshOutputStream();
	}
	
	private Prompt createPrompt() {
        List<TerminalCharacter> chars = new ArrayList<TerminalCharacter>();
        chars.add(new TerminalCharacter('[', Color.DEFAULT_BG, Color.BLUE_TEXT));
        chars.add(new TerminalCharacter('t', Color.DEFAULT_BG, Color.RED_TEXT,
                CharacterType.ITALIC));
        chars.add(new TerminalCharacter('e', Color.DEFAULT_BG, Color.RED_TEXT,
                CharacterType.INVERT));
        chars.add(new TerminalCharacter('s', Color.DEFAULT_BG, Color.RED_TEXT,
                CharacterType.CROSSED_OUT));
        chars.add(new TerminalCharacter('t', Color.DEFAULT_BG ,Color.RED_TEXT,
                CharacterType.BOLD));
        chars.add(new TerminalCharacter(']', Color.DEFAULT_BG, Color.BLUE_TEXT,
                CharacterType.PLAIN));
        chars.add(new TerminalCharacter('$', Color.DEFAULT_BG, Color.WHITE_TEXT,
                CharacterType.UNDERLINE));
        chars.add(new TerminalCharacter(' ', Color.DEFAULT_BG, Color.WHITE_TEXT));
        return new Prompt(chars);
	}
	
	private ConsoleCallback createConsoleCallback() {
		return new ConsoleCallback() {
			@Override
			public int readConsoleOutput(ConsoleOutput output)
					throws IOException {
				console.pushToStdOut("hoorray\n");
				return 0;
			}
		};
	}
	
	public void start() {
		try {
			console.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendInput(String input) {
		inputStream.append(input);
	}
	
	public void stop() {
		try {
			console.stop();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
