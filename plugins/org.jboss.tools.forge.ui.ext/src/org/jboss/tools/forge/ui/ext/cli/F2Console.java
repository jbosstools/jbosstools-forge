package org.jboss.tools.forge.ui.ext.cli;

import java.io.File;

import org.jboss.forge.addon.shell.ShellHandle;
import org.jboss.forge.furnace.util.OperatingSystemUtils;
import org.jboss.tools.aesh.core.console.AeshConsole;
import org.jboss.tools.forge.ext.core.FurnaceService;

public class F2Console extends AeshConsole {
	private ShellHandle handle;

	protected void createConsole() {
		// super.createConsole();
		handle = FurnaceService.INSTANCE.lookup(ShellHandle.class);
		File currentDir = OperatingSystemUtils.getUserHomeDir();
		handle.initialize(currentDir, getInputStream(), getStdOut(), getStdErr());
	}

	public void start() {
		// super.start();
	}

	@Override
	public void stop() {
		handle.destroy();
		handle = null;
	}
}
