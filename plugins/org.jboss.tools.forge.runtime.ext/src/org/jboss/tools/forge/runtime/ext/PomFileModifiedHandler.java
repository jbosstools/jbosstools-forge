package org.jboss.tools.forge.runtime.ext;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.forge.resources.events.ResourceEvent;
import org.jboss.forge.shell.Shell;

public class PomFileModifiedHandler {
	
	private static final String ESCAPE = new String(new char[] { 27, '%'} );
	
	@Inject
	private Shell shell;
	
	public void handleResourceChanged(@Observes ResourceEvent event) {
		if (event.getResource() != null && "pom.xml".equals(event.getResource().getName())) {
			sendEscaped("POM File Modified: " + event.getResource().getParent().getName()); 
		}
	}
	
	private void sendEscaped(String str) {
		shell.print(ESCAPE + str + ESCAPE); 
	}

}
