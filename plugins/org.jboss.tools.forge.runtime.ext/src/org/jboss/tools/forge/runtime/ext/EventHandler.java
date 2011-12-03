package org.jboss.tools.forge.runtime.ext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.forge.project.Project;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.events.CommandExecuted;

public class EventHandler {
	
	private static final String ESCAPE = new String(new char[] { 27, '%'} );
	
	@Inject
	private Shell shell;
	
	public void handleCommandExecuted(@Observes CommandExecuted event) {
		Resource<?> currentResource = shell.getCurrentResource();
		String currentResourceName = currentResource.getFullyQualifiedName();
		String currentResourceType = currentResource.getClass().getSimpleName();
		Project project = shell.getCurrentProject();
		String projectName = "";
		if (project != null) {
			projectName = project.getProjectRoot().getFullyQualifiedName();
		}
		String parameterString = "";
		try {
			Method method = event.getClass().getMethod("getParameters", new Class[] {});
			Object object = method.invoke(event, new Object[] {});
			if (object instanceof Object[]) {
				Object[] parameters = (Object[])object;
				for (Object parameter : parameters) {
					parameterString += parameter + " ";
				}
			}
		} catch (NoSuchMethodException e) {
		} catch (InvocationTargetException e) {
		} catch (IllegalAccessException e) {}
		
		String command = event.getCommand().getParent().getName() + " " + event.getCommand().getName();
		sendEscaped(
				" EC: " + command + 
				" CRN: " + currentResourceName + 
				" CRT: " + currentResourceType + 
				" CPN: " + projectName +
				" PAR: " + parameterString);
	}
	
	private void sendEscaped(String str) {
		shell.print(ESCAPE + str + ESCAPE); 
	}

}
