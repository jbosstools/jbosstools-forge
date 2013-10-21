package org.jboss.tools.forge.ui.ext.cli;

import org.jboss.forge.addon.shell.Shell;
import org.jboss.forge.addon.shell.ShellFactory;
import org.jboss.forge.furnace.util.OperatingSystemUtils;
import org.jboss.tools.aesh.core.console.AeshConsole;
import org.jboss.tools.forge.ext.core.FurnaceService;

public class F2Console extends AeshConsole {
	private Shell shell;

	protected void createConsole() {
		// super.createConsole();
		ShellFactory shellFactory = FurnaceService.INSTANCE
				.lookup(ShellFactory.class);
		shell = shellFactory.createShell(OperatingSystemUtils.getUserHomeDir(),
				createAeshSettings());
	}

	public void start() {
		// Already started
		if (shell == null)
			createConsole();

//		super.start();
	}

	@Override
	public void stop() {
		shell.close();
		shell = null;
	}
}
