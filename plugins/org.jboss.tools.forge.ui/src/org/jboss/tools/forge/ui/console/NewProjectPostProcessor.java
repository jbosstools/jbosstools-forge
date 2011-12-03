package org.jboss.tools.forge.ui.console;

import java.util.Map;

import org.jboss.tools.forge.importer.ProjectImporter;


public class NewProjectPostProcessor implements ForgeCommandPostProcessor {

	@Override
	public void postProcess(Map<String, String> commandDetails) {
		String projectPath = commandDetails.get("cpn");
		int index = projectPath.lastIndexOf('/');
		String projectDirName = projectPath.substring(index + 1);
		String projectBaseDirPath = projectPath.substring(0, index);
		ProjectImporter importer = new ProjectImporter(projectBaseDirPath, projectDirName);
		importer.importProject();
	}

}
