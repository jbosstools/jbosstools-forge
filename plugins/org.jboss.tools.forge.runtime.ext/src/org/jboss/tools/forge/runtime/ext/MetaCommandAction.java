package org.jboss.tools.forge.runtime.ext;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;

import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.command.CommandMetadata;
import org.jboss.forge.shell.command.PluginMetadata;
import org.jboss.forge.shell.command.PluginRegistry;
import org.jboss.forge.shell.spi.TriggeredAction;

public class MetaCommandAction implements TriggeredAction {

	private static final String ESCAPE = new String(new char[] { 27, '%' });

	@Inject Shell shell;
	
	@Inject PluginRegistry registry;
	
	

	@Override
	public ActionListener getListener() {
		return new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					shell.print(ESCAPE);
					String text = shell.readLine();
					shell.print(ESCAPE);
					handleHiddenCommand(text);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		};
	}
	
	private void handleHiddenCommand(String text) {	
		try {
			FileWriter fileWriter = new FileWriter("/Users/koen/Temp/handleHiddenCommand.txt", true);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			bufferedWriter.write("handleHiddenCommand(" + text + ")\n");
			if ("plugin-candidates-query".equals(text)) {
				bufferedWriter.write("query is plugin-candidates-query\n");
				shell.print(ESCAPE + "plugin-candidates-answer: " + getPluginCandidates() + ESCAPE);
				bufferedWriter.write("response sent back to tools\n");
			}
			bufferedWriter.write("about to exit handleHiddenCommand\n");
			bufferedWriter.flush();
//			fileWriter.close();
		} catch (Exception e) {
			// ignored
		}
	}
	
	private String getPluginCandidates() {
		try {
			FileWriter fileWriter = new FileWriter("/Users/koen/Temp/getPluginCandidates.txt", true);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			bufferedWriter.write("getPluginCandidates()\n");
			StringBuffer resultBuffer = new StringBuffer();
			Map<String, List<PluginMetadata>> plugins = registry.getPlugins();
			bufferedWriter.write("got the plugins\n");
			for (Entry<String, List<PluginMetadata>> entry : plugins.entrySet()) {
				bufferedWriter.write("processing entry : " + entry.getKey() + "\n");
				for (PluginMetadata pluginMeta : entry.getValue()) {
					bufferedWriter.write("processing pluginMeta : " + pluginMeta.getName());
					if (pluginMeta.constrantsSatisfied(shell)) {
						bufferedWriter.write("pluginMeta : " + pluginMeta.getName() + " satisfies constraints\n");
						List<CommandMetadata> commands = pluginMeta.getAllCommands();
						bufferedWriter.write("got the commands\n");
						if (!commands.isEmpty()) {
							bufferedWriter.write("commands is not empty\n");
							resultBuffer.append("p:").append(pluginMeta.getName()).append(' ');
							bufferedWriter.write("result becomes : " + resultBuffer.toString() + "\n");
							for (CommandMetadata commandMeta : commands) {
								bufferedWriter.write("processing command : " + commandMeta.getName() + "\n");
								resultBuffer.append("c:").append(commandMeta.getName()).append(' ');
								bufferedWriter.write("result becomes : " + resultBuffer.toString() + "\n");
							}
						}
					}
				}
			}
			bufferedWriter.write("about to return result : " + resultBuffer.toString() + "\n");
			bufferedWriter.flush();
			return resultBuffer.toString();
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public char getTrigger() {
		return (char)31;
	}

}
