package org.jboss.tools.forge.ui.console;

import org.jboss.tools.forge.importer.ProjectImporter;

public class NewProjectPostProcessor implements ForgeCommandPostProcessor {

	@Override
	public void postProcessCommand(String command, String output) {
		int index = output.lastIndexOf("***SUCCESS*** Created project [");
		if (index == -1) return;
		if (index + 31 > output.length()) return;
		output = output.substring(index + 31);
		index = output.lastIndexOf("] in new working directory [");
		if (index == -1) return;
		if (index + 28 > output.length()) return;
		output = output.substring(index + 28);
		index = output.indexOf("]");
		if (index == -1) return;
		String projectPath = output.substring(0, index);
		index = projectPath.lastIndexOf('/');
		String projectDirName = projectPath.substring(index + 1);
		String projectBaseDirPath = projectPath.substring(0, index);
		ProjectImporter importer = new ProjectImporter(projectBaseDirPath, projectDirName);
		importer.importProject();
	}

}
