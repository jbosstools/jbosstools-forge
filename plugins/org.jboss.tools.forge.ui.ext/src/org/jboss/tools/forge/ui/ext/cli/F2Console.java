package org.jboss.tools.forge.ui.ext.cli;

import org.jboss.tools.aesh.core.console.AeshConsole;

public class F2Console extends AeshConsole {
	// private Shell shell;

	protected void createConsole() {
		super.createConsole();
		// ShellFactory shellFactory = FurnaceService.INSTANCE
		// .lookup(ShellFactory.class);
		// shell =
		// shellFactory.createShell(OperatingSystemUtils.getUserHomeDir(),
		// createAeshSettings());
	}

	public void start() {
		// Already started
		// if (shell == null)
		// createConsole();

		super.start();
	}

	@Override
	public void stop() {
		super.stop();
		// shell.close();
		// shell = null;
	}
}
