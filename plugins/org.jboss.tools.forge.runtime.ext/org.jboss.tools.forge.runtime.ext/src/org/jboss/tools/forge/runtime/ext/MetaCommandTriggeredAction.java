package org.jboss.tools.forge.runtime.ext;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.inject.Inject;

import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.command.PluginRegistry;
import org.jboss.forge.shell.spi.TriggeredAction;

public class MetaCommandTriggeredAction implements TriggeredAction {

	private static final String ESCAPE = new String(new char[] { 27, '[', '%' });

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
		if ("command-list-query".equals(text)) {
			shell.print(ESCAPE + "command-list-answer: " + getCommandList() + ESCAPE);
		}
	}
	
	private String getCommandList() {
		StringBuffer resultBuffer = new StringBuffer();
		for (String name : registry.getPlugins().keySet()) {
			resultBuffer.append(name).append(" ");
		}
		return resultBuffer.toString();
	}

	@Override
	public char getTrigger() {
		return (char)31;
	}

}
