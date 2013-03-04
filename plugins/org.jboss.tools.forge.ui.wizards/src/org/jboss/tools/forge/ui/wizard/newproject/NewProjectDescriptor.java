package org.jboss.tools.forge.ui.wizard.newproject;

import org.eclipse.core.resources.ResourcesPlugin;

public class NewProjectDescriptor {
	
	public String name;
	public String location = 
			ResourcesPlugin
			.getWorkspace()
			.getRoot()
			.getLocation()
			.toOSString(); // absolute path of the project location

}
