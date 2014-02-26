package org.jboss.tools.aesh.example;

import java.io.IOException;
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
import org.jboss.tools.aesh.core.console.AbstractAeshConsole;

public class ExampleConsole extends AbstractAeshConsole {

	private org.jboss.aesh.console.AeshConsole console;

	public void start() {
		if (console == null) {
			createConsole();
		}
		console.start();
	}

	public void stop() {
		console.stop();
	}

	public void createConsole() {
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
					.generateCommand();
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

	private Settings createAeshSettings() {
		return new SettingsBuilder()
			.inputStream(getInputStream())
			.outputStream(new PrintStream(getStdOut()))
			.outputStreamError(new PrintStream(getStdErr()))
			.create();
	}


	
}
