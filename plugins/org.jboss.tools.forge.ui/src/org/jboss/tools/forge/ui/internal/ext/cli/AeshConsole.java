package org.jboss.tools.forge.ui.internal.ext.cli;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;

import org.eclipse.core.resources.ResourcesPlugin;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.shell.ShellHandle;
import org.jboss.tools.aesh.core.console.AbstractConsole;
import org.jboss.tools.forge.core.furnace.FurnaceService;

public class AeshConsole extends AbstractConsole {
	
	private ShellHandle handle;
	private CommandLineListener executionListener = new CommandLineListener();

	public void start() {
		handle = FurnaceService.INSTANCE.lookup(ShellHandle.class);
		File currentDir = ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile();
		OutputStream stdOut = getOutputStream();
		OutputStream stdErr = getErrorStream();
		PrintStream out = new PrintStream(stdOut, true);
		PrintStream err = new PrintStream(stdErr, true);
		handle.initialize(currentDir, getInputStream(), out, err);
		handle.addCommandExecutionListener(executionListener);	
		ProjectFactory projectFactory = FurnaceService.INSTANCE.lookup(ProjectFactory.class);
		projectFactory.addProjectListener(executionListener);
	}

	@Override
	public void stop() {
		handle.destroy();
		handle = null;
	}
}
