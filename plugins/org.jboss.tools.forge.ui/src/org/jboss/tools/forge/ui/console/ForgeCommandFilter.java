package org.jboss.tools.forge.ui.console;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.jboss.tools.forge.core.io.ForgeHiddenOutputFilter;
import org.jboss.tools.forge.core.io.ForgeOutputListener;
import org.jboss.tools.forge.importer.ProjectConfigurationUpdater;

public class ForgeCommandFilter extends ForgeHiddenOutputFilter {
	
	private ForgeCommandProcessor commandProcessor = new ForgeCommandProcessor();
	
	public ForgeCommandFilter(ForgeOutputListener listener) {
		super(listener);
		
	}

	@Override
	public void handleFilteredString(String str) {
		if (str.startsWith(" EC: ")) {
			commandProcessor.postProcess(str);
		} else if (str.startsWith("POM File Modified: ")) {
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(str.substring(19));
			if (project != null) {
				ProjectConfigurationUpdater.updateProject(project);
			}
		}
	}

}
