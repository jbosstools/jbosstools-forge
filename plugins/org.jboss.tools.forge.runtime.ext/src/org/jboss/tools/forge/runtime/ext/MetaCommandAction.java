package org.jboss.tools.forge.runtime.ext;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.forge.ForgeEnvironment;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.command.CommandMetadata;
import org.jboss.forge.shell.command.PluginMetadata;
import org.jboss.forge.shell.command.PluginRegistry;
import org.jboss.forge.shell.events.AcceptUserInput;
import org.jboss.forge.shell.events.PreStartup;
import org.jboss.forge.shell.spi.TriggeredAction;

public class MetaCommandAction implements TriggeredAction {

	private static final String ESCAPE = new String(new char[] { 27, '%' });
	
	private static boolean INSTALLING_PLUGIN = false;

	@Inject
	Shell shell;

	@Inject
	PluginRegistry registry;

	@Override
	public ActionListener getListener() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				synchronized (shell) {
					EventHandler.setEnabled(false);
					try {
						shell.print(ESCAPE);
						String text = shell.readLine();
						shell.print(ESCAPE);
						handleHiddenCommand(text);
					} catch (IOException e1) {
						e1.printStackTrace();
					} finally {
						EventHandler.setEnabled(true);
					}
				}
			}
		};
	}

	private void handleHiddenCommand(String text) {
		shell.print(ESCAPE + "handleHiddenCommand(" + text + ")" + ESCAPE);
		try {
			if ("plugin-candidates-query".equals(text)) {
				shell.print(ESCAPE + "RESULT: plugin-candidates-answer: "
						+ getPluginCandidates() + ESCAPE);
			} else if (text.startsWith("forge install-plugin")) {
				shell.print(ESCAPE + "i need to install a plugin" + ESCAPE);
 				executePluginInstallation(text);
			} else if (text.equals("get-prompt")) {
				shell.print(ESCAPE + "RESULT: " + environment.getProperty("PROMPT") + ESCAPE);
			} else if (text.equals("get-prompt-no-project")) {
				shell.print(ESCAPE + "RESULT: " + environment.getProperty("PROMPT_NOPROJ") + ESCAPE);
			} else if (text.startsWith("set-prompt-no-project")) {
				shell.print(ESCAPE + "prompt-no-project: " + text.substring(22) + ESCAPE);
//				environment.setProperty("PROMPT_NOPROJ", text.substring(22));
				shell.print(ESCAPE + "RESULT: set-prompt-no-project" + ESCAPE);
			} else if (text.startsWith("set-prompt")) {
				shell.print(ESCAPE + "prompt: " + text.substring(11) + ESCAPE);
//				environment.setProperty("PROMPT", text.substring(11));
				shell.print(ESCAPE + "RESULT: set-prompt" + ESCAPE);
			} else {
				executeCommand(text);
			}
		} catch (Exception e) {
			// ignored
		}
	}

	private void executeCommand(String text) throws Exception {
		try {
			EventHandler.setEnabled(false);
			shell.print(ESCAPE + "RESULT: ");
			shell.execute(text);
			shell.print(ESCAPE);
		} finally {
			EventHandler.setEnabled(true);
		}
	}

	private void executePluginInstallation(String text) throws Exception {
		INSTALLING_PLUGIN = true;
		EventHandler.setEnabled(false);
		shell.print(ESCAPE + "RESULT: ");
		shell.execute(text);
	}

	public void onPreStartup(@Observes PreStartup preStartup) {
		if (INSTALLING_PLUGIN) {
			EventHandler.setEnabled(false);
		}
	}
	
	@Inject
	ForgeEnvironment environment;

	public void onPostStartup(@Observes AcceptUserInput postStartup, final Shell shell) {
		if (INSTALLING_PLUGIN) {
//			environment.setProperty("PROMPT", "");
//			environment.setProperty("PROMPT_NOPROJ", "");
			shell.print(ESCAPE);
			INSTALLING_PLUGIN = false;
			EventHandler.setEnabled(true);
		}
	}

	private String getPluginCandidates() {
		try {
			StringBuffer resultBuffer = new StringBuffer();
			Map<String, List<PluginMetadata>> plugins = registry.getPlugins();
			for (Entry<String, List<PluginMetadata>> entry : plugins.entrySet()) {
				for (PluginMetadata pluginMeta : entry.getValue()) {
					if (pluginMeta.constrantsSatisfied(shell)) {
						List<CommandMetadata> commands = pluginMeta
								.getAllCommands();
						if (!commands.isEmpty()) {
							resultBuffer.append("p:")
									.append(pluginMeta.getName()).append(' ');
							for (CommandMetadata commandMeta : commands) {
								resultBuffer.append("c:")
										.append(commandMeta.getName())
										.append(' ');
							}
						}
					}
				}
			}
			return resultBuffer.toString();
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public char getTrigger() {
		return (char) 31;
	}

}
