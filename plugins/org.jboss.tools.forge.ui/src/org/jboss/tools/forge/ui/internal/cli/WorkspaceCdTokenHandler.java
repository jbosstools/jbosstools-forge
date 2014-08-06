/**
 * Copyright (c) Red Hat, Inc., contributors and others 2013 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.forge.ui.internal.cli;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.shell.spi.command.CdTokenHandler;
import org.jboss.forge.addon.ui.context.UIContext;

/**
 * Handles the '#' workspace shortcut character in JBossTools Forge CLI
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class WorkspaceCdTokenHandler implements CdTokenHandler {
	private ResourceFactory resourceFactory;

	public WorkspaceCdTokenHandler(ResourceFactory resourceFactory) {
		this.resourceFactory = resourceFactory;
	}

	@Override
	public List<Resource<?>> getNewCurrentResources(UIContext context,
			String token) {
		List<Resource<?>> result = new ArrayList<>();
		if (token.startsWith("#")) {
			IPath location;
			String projectName = token.replaceFirst("#/?", "");
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			if (projectName.isEmpty()) {
				location = root.getLocation();
			} else {
				location = root.getProject(projectName).getLocation();
			}
			if (location != null) {
				File file = location.makeAbsolute().toFile();
				Resource<File> resource = resourceFactory.create(file);
				result.add(resource);
			}
		}
		return result;
	}
}