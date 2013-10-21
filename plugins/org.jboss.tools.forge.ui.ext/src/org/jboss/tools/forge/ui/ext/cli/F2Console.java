package org.jboss.tools.forge.ui.ext.cli;

import java.io.File;

import org.jboss.forge.addon.shell.ShellHolder;
import org.jboss.forge.furnace.util.OperatingSystemUtils;
import org.jboss.tools.aesh.core.console.AeshConsole;
import org.jboss.tools.forge.ext.core.FurnaceService;

public class F2Console extends AeshConsole {
	private ShellHolder shell;

	protected void createConsole() {
		// super.createConsole();
		shell = FurnaceService.INSTANCE.lookup(ShellHolder.class);
		File currentDir = OperatingSystemUtils.getUserHomeDir();
		shell.initialize(currentDir, getInputStream(), getStdOut(), getStdErr());
	}

	public void start() {
		// super.start();
	}

	@Override
	public void stop() {
		shell.destroy();
		shell = null;
	}
}
