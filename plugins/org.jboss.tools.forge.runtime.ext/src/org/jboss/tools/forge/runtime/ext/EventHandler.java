package org.jboss.tools.forge.runtime.ext;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.forge.project.Project;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.events.CommandExecuted;

public class EventHandler {
	
	private static final String ESCAPE = new String(new char[] { 27, '%'} );
	
	@Inject
	private Shell shell;
	
	public void handleCommandExecuted(@Observes CommandExecuted event) {
		Project project = shell.getCurrentProject();
		String str = "no project";
		if (project != null) {
			str = project.getProjectRoot().getName();
		}
		String command = event.getCommand().getParent().getName() + " " + event.getCommand().getName();
		sendEscaped("Executed Command: " + command + " Current Project Directory: " + str);
	}
	
	private void sendEscaped(String str) {
		shell.print(ESCAPE + str + ESCAPE); 
	}

}
