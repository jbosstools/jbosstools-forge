package org.jboss.tools.forge.runtime.ext;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.events.PostStartup;

@ApplicationScoped
public class EventHandler {
	
	private final static String ESCAPE_START = new String(new char[] { 27, '[', '%'});
	private final static String ESCAPE_END = "%";
	
	@Inject
	Shell shell;
	
	public void handlePostStartup(@Observes PostStartup event) {
//		sendEscapedSequence("PostStartup");
	}
	
	private void sendEscapedSequence(String sequence) {
		shell.print(ESCAPE_START + sequence + ESCAPE_END);
	}

}
