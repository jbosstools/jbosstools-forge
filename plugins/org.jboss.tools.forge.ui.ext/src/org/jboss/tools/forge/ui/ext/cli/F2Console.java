package org.jboss.tools.forge.ui.ext.cli;

import org.jboss.forge.addon.shell.ForgeShell;
import org.jboss.tools.aesh.core.console.AeshConsole;
import org.jboss.tools.forge.ext.core.FurnaceService;

public class F2Console extends AeshConsole {
	
	private ForgeShell shell;

	protected void createConsole() {
		shell = FurnaceService.INSTANCE.lookup(ForgeShell.class);
		shell.setInputStream(getInputStream());
		shell.setStdOut(getStdOut());
		shell.setStdErr(getStdErr());
	}
	
	public void start() {
		try {
			shell.startShell();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
