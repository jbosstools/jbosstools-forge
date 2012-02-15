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
			return doOpenLine(line);
		} else if (line.startsWith("cd ")) {
			return doCdLine(line);
		} else if (line.startsWith("pick-up ")) {
			return doPickUpLine(line);
		}
		return line;
	}
	
	private String doCdLine(String line) {
		return "cd " + handleWorkspaceShortCut(line.substring(3));
	}
	
	private String doPickUpLine(String line) {
		return "pick-up " + handleWorkspaceShortCut(line.substring(8));
	}
	
	private String doOpenLine(String line) {
		String crn = shell.getCurrentResource().getFullyQualifiedName();
		String crt = shell.getCurrentResource().getClass().getSimpleName();
		Project project = shell.getCurrentProject();
		String cpn = "";
		if (project != null) {
			cpn = project.getProjectRoot().getFullyQualifiedName();
		}
		String par = handleWorkspaceShortCut(line.substring(5));
		sendEscaped(
				" EC: " + line + 
				" CRN: " + crn + 
				" CRT: " + crt + 
				" CPN: " + cpn +
				" PAR: " + par);
		return "\n";
	}
	
	private String handleWorkspaceShortCut(String str) {
		if (str.startsWith("#")) {
			return System.getProperty("forge.workspace") + str.substring(1);
		}
		return str;
	}

	private void sendEscaped(String str) {
		shell.print(ESCAPE + str + ESCAPE); 
	}

}
