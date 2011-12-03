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
		if (line.startsWith("open ")) {
			String crn = shell.getCurrentResource().getFullyQualifiedName();
			String crt = shell.getCurrentResource().getClass().getSimpleName();
			Project project = shell.getCurrentProject();
			String cpn = "";
			if (project != null) {
				cpn = project.getProjectRoot().getFullyQualifiedName();
			}
			String par = line.substring(5);
			sendEscaped(
					" EC: " + line + 
					" CRN: " + crn + 
					" CRT: " + crt + 
					" CPN: " + cpn +
					" PAR: " + par);
			return "\n";
		}
		return line;
	}

	private void sendEscaped(String str) {
		shell.print(ESCAPE + str + ESCAPE); 
	}

}
