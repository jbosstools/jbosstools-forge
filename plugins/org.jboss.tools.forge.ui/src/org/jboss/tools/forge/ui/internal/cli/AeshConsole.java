/**
 * Copyright (c) Red Hat, Inc., contributors and others 2013 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.forge.ui.internal.cli;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;

import org.eclipse.core.resources.ResourcesPlugin;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.shell.ShellHandle;
import org.jboss.forge.addon.shell.spi.command.CdTokenHandler;
import org.jboss.forge.addon.shell.spi.command.CdTokenHandlerFactory;
import org.jboss.forge.addon.ui.command.AbstractCommandExecutionListener;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.furnace.spi.ListenerRegistration;
import org.jboss.tools.aesh.core.console.AbstractConsole;
import org.jboss.tools.forge.core.furnace.FurnaceService;

public class AeshConsole extends AbstractConsole {

	private ShellHandle handle;
	private CommandLineListener executionListener = new CommandLineListener();
	private ListenerRegistration<CdTokenHandler> tokenHandler;

	private Resource<?> currentResource;

	public void start() {
		handle = FurnaceService.INSTANCE.lookup(ShellHandle.class);
		ResourceFactory resourceFactory = FurnaceService.INSTANCE
				.lookup(ResourceFactory.class);
		final File currentDir = ResourcesPlugin.getWorkspace().getRoot()
				.getLocation().toFile();
		currentResource = resourceFactory.create(currentDir);
		OutputStream stdOut = getOutputStream();
		OutputStream stdErr = getErrorStream();
		PrintStream out = new PrintStream(stdOut, true);
		PrintStream err = new PrintStream(stdErr, true);
		handle.initialize(currentDir, getInputStream(), out, err);
		handle.addCommandExecutionListener(executionListener);
		// Listening for selection events
		handle.addCommandExecutionListener(new AbstractCommandExecutionListener() {
			@Override
			public void postCommandExecuted(UICommand command,
					UIExecutionContext context, Result result) {
				currentResource = context.getUIContext().getSelection();
			}
		});

		ProjectFactory projectFactory = FurnaceService.INSTANCE
				.lookup(ProjectFactory.class);
		projectFactory.addProjectListener(executionListener);
		CdTokenHandlerFactory tokenHandlerFactory = FurnaceService.INSTANCE
				.lookup(CdTokenHandlerFactory.class);
		tokenHandler = tokenHandlerFactory
				.addTokenHandler(new WorkspaceCdTokenHandler(resourceFactory));
	}

	@Override
	public void stop() {
		handle.destroy();
		handle = null;
		if (tokenHandler != null)
			tokenHandler.removeListener();
		tokenHandler = null;
	}

	@Override
	public Resource<?> getCurrentResource() {
		return currentResource;
	}
}
