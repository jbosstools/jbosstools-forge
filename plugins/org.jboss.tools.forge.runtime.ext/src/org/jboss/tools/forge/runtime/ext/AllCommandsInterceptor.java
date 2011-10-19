package org.jboss.tools.forge.runtime.ext;

import javax.inject.Inject;

import org.jboss.forge.project.Project;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.spi.CommandInterceptor;

public class AllCommandsInterceptor implements CommandInterceptor {

	private static final String ESCAPE = new String(new char[] { 27, '%'} );
	
	@Inject
	private Shell shell;
	
	@Override
	public String intercept(String line) {
		Project project = shell.getCurrentProject();
		String str = "no project";
		if (project != null) {
			str = project.getProjectRoot().getName();
		}
		sendEscaped("Intercepted Command: " + line + " Current Project Directory: " + str);
		return line;
	}

	private void sendEscaped(String str) {
		shell.print(ESCAPE + str + ESCAPE); 
	}

}
