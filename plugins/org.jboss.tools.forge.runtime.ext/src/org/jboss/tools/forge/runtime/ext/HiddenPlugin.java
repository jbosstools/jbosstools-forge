package org.jboss.tools.forge.runtime.ext;

import javax.inject.Inject;

import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.DefaultCommand;
import org.jboss.forge.shell.plugins.Plugin;


public class HiddenPlugin implements Plugin {
	
	private final static String ESCAPE_START = new String(new char[] { 27, '[', '%'});
	private final static String ESCAPE_END = "%";
	
	@Inject
	Shell shell;
	
	@DefaultCommand
	public void run() {
		sendEscapedSequence("blah!");
	}

	private void sendEscapedSequence(String sequence) {
		shell.print(ESCAPE_START + sequence + ESCAPE_END);
	}

}
