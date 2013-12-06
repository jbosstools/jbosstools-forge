package org.jboss.tools.aesh.core.console;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.jboss.aesh.cl.builder.CommandBuilder;
import org.jboss.aesh.cl.builder.OptionBuilder;
import org.jboss.aesh.cl.exception.CommandLineParserException;
import org.jboss.aesh.cl.exception.OptionParserException;
import org.jboss.aesh.cl.internal.ProcessedCommand;
import org.jboss.aesh.console.AeshConsoleBuilder;
import org.jboss.aesh.console.Prompt;
import org.jboss.aesh.console.command.Command;
import org.jboss.aesh.console.command.CommandResult;
import org.jboss.aesh.console.command.invocation.CommandInvocation;
import org.jboss.aesh.console.command.registry.AeshCommandRegistryBuilder;
import org.jboss.aesh.console.command.registry.CommandRegistry;
import org.jboss.aesh.console.settings.Settings;
import org.jboss.aesh.console.settings.SettingsBuilder;
import org.jboss.aesh.terminal.CharacterType;
import org.jboss.aesh.terminal.Color;
import org.jboss.aesh.terminal.TerminalCharacter;
import org.jboss.aesh.terminal.TerminalColor;
import org.jboss.tools.aesh.core.io.AeshInputStream;
import org.jboss.tools.aesh.core.io.AeshOutputStream;
import org.jboss.tools.aesh.core.io.AeshOutputStream.StreamListener;

public class AeshConsole {

	private AeshInputStream inputStream;
	private AeshOutputStream stdOut, stdErr;
	private org.jboss.aesh.console.AeshConsole console;

	public AeshConsole() {
		initialize();
	}

	protected void initialize() {
		createStreams();
		createConsole();
	}

	protected void createConsole() {
		ProcessedCommand fooCommand;
		try {
			fooCommand = new CommandBuilder()
					.name("foo")
					.description("fooing")
					.addOption(
							new OptionBuilder().name("bar")
									.addDefaultValue("en 1 0")
									.addDefaultValue("to 2 0").fieldName("bar")
									.type(String.class).create())
					.generateParameter();
			CommandRegistry registry = new AeshCommandRegistryBuilder()
					.command(fooCommand, FooCommand.class).create();
			console = new AeshConsoleBuilder().commandRegistry(registry)
					.settings(createAeshSettings()).prompt(createPrompt())
					.create();
		} catch (OptionParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CommandLineParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// this command use a builder defined above to specify the meta data needed
	public static class FooCommand implements Command<CommandInvocation> {

		private String bar;

		@Override
		public CommandResult execute(CommandInvocation commandInvocation)
				throws IOException {
			if (bar == null)
				commandInvocation.getShell().out().println("NO BAR!");
			else
				commandInvocation.getShell().out()
						.println("you set bar to: " + bar);
			return CommandResult.SUCCESS;
		}
	}

	protected Settings createAeshSettings() {
		return new SettingsBuilder()
			.inputStream(inputStream)
			.outputStream(new PrintStream(stdOut))
			.outputStreamError(new PrintStream(stdErr))
			.create();
	}

	protected void createStreams() {
		inputStream = new AeshInputStream();
		stdOut = new AeshOutputStream();
		stdErr = new AeshOutputStream();
	}

	private Prompt createPrompt() {
		List<TerminalCharacter> chars = new ArrayList<TerminalCharacter>();
		chars.add(new TerminalCharacter('[', new TerminalColor(Color.DEFAULT, Color.BLUE)));
		chars.add(new TerminalCharacter('t', new TerminalColor(Color.DEFAULT, Color.RED),
				CharacterType.ITALIC));
		chars.add(new TerminalCharacter('e', new TerminalColor(Color.DEFAULT, Color.RED),
				CharacterType.INVERT));
		chars.add(new TerminalCharacter('s', new TerminalColor(Color.DEFAULT, Color.RED),
				CharacterType.CROSSED_OUT));
		chars.add(new TerminalCharacter('t', new TerminalColor(Color.DEFAULT, Color.RED),
				CharacterType.BOLD));
		chars.add(new TerminalCharacter(']', new TerminalColor(Color.DEFAULT, Color.BLUE),
				CharacterType.FAINT));
		chars.add(new TerminalCharacter('$', new TerminalColor(Color.DEFAULT,
				Color.WHITE), CharacterType.UNDERLINE));
		chars.add(new TerminalCharacter(' ', new TerminalColor(Color.DEFAULT, Color.WHITE)));
		return new Prompt(chars);
	}

	public void start() {
		console.start();
	}

	public void sendInput(String input) {
		inputStream.append(input);
	}

	public void stop() {
		console.stop();
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
