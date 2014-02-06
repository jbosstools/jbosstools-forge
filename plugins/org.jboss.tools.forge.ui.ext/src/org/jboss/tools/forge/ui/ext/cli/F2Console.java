package org.jboss.tools.forge.ui.ext.cli;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;

import org.jboss.forge.addon.shell.ShellHandle;
import org.jboss.forge.furnace.util.OperatingSystemUtils;
import org.jboss.tools.aesh.core.console.AeshConsole;
import org.jboss.tools.forge.ext.core.FurnaceService;

public class F2Console extends AeshConsole {
	
	private ShellHandle handle;

	protected void initialize() {
		createStreams();
	}

	protected void createConsole() {
		// super.createConsole();
		handle = FurnaceService.INSTANCE.lookup(ShellHandle.class);
		File currentDir = OperatingSystemUtils.getUserHomeDir();
		OutputStream stdOut = getStdOut();
		OutputStream stdErr = getStdErr();

		PrintStream out = new PrintStream(stdOut, true);
		PrintStream err = new PrintStream(stdErr, true);

		handle.initialize(currentDir, getInputStream(), out, err);
	}

	public void start() {
		createConsole();
	}

	@Override
	public void stop() {
		handle.destroy();
		handle = null;
	}
}
