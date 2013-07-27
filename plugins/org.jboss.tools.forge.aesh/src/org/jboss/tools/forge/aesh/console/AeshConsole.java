package org.jboss.tools.forge.aesh.console;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jboss.aesh.console.Console;
import org.jboss.aesh.console.ConsoleCallback;
import org.jboss.aesh.console.ConsoleOutput;
import org.jboss.aesh.console.Prompt;
import org.jboss.aesh.console.settings.Settings;
import org.jboss.aesh.terminal.CharacterType;
import org.jboss.aesh.terminal.Color;
import org.jboss.aesh.terminal.TerminalCharacter;
import org.jboss.tools.forge.aesh.io.AeshInputStream;
import org.jboss.tools.forge.aesh.io.AeshOutputStream;
import org.jboss.tools.forge.aesh.io.AeshOutputStream.StreamListener;

public class AeshConsole {
	
	protected AeshInputStream inputStream;
	protected AeshOutputStream stdOut, stdErr;
	
	public AeshConsole() {
		initialize();
	}
	
	protected void initialize() {
		try {
			inputStream = new AeshInputStream();
			Settings.getInstance().setInputStream(inputStream);
			stdOut = new AeshOutputStream();
			Settings.getInstance().setStdOut(stdOut);
			stdErr = new AeshOutputStream();
			Settings.getInstance().setStdErr(stdErr);
			Console.getInstance().setPrompt(createPrompt());
			Console.getInstance().setConsoleCallback(createConsoleCallback());
		} catch (IOException e) {
			e.printStackTrace();
		}
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

//        final Prompt prompt = new Prompt(chars);
//        List<TerminalCharacter> chars = new ArrayList<TerminalCharacter>();
//        chars.add(new TerminalCharacter('['));
//        chars.add(new TerminalCharacter('t'));
//        chars.add(new TerminalCharacter('e'));
//        chars.add(new TerminalCharacter('s'));
//        chars.add(new TerminalCharacter('t'));
//        chars.add(new TerminalCharacter(']'));
//        chars.add(new TerminalCharacter('$'));
//        chars.add(new TerminalCharacter(' '));
        return new Prompt(chars);
	}
	
	private ConsoleCallback createConsoleCallback() {
		return new ConsoleCallback() {
			@Override
			public int readConsoleOutput(ConsoleOutput output)
					throws IOException {
				Console.getInstance().pushToStdOut("hoorray\n");
				return 0;
			}
		};
	}
	
	public void start() {
		try {
			Console.getInstance().start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendInput(String input) {
		inputStream.append(input);
	}
	
	public void stop() {
		try {
			Console.getInstance().stop();
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
	
}
