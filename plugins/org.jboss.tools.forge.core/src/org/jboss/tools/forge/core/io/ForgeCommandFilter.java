/**
 * Copyright (c) Red Hat, Inc., contributors and others 2004 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.forge.core.io;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.jboss.tools.forge.core.util.ProjectTools;

public class ForgeCommandFilter extends ForgeHiddenOutputFilter {
	
	private ForgeCommandProcessor commandProcessor = null;
	
	public ForgeCommandFilter(ForgeOutputListener listener, ForgeCommandProcessor commandProcessor) {
		super(listener);
		this.commandProcessor = commandProcessor;
		
	}

	@Override
	public void handleFilteredString(String str) {
		if (str.startsWith(" EC: ")) {
			commandProcessor.postProcess(str);
		} else if (str.startsWith("POM File Modified: ")) {
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(str.substring(19));
			if (project != null) {
				ProjectTools.updateProjectConfiguration(project);
			}
		}
	}
	
}
