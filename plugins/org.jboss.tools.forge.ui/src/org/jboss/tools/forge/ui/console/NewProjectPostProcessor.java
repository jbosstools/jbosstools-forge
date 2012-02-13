package org.jboss.tools.forge.ui.console;

import java.io.File;
import java.util.Map;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.jboss.tools.forge.core.preferences.ForgeRuntimesPreferences;
import org.jboss.tools.forge.core.process.ForgeRuntime;
import org.jboss.tools.forge.importer.ProjectImporter;
import org.jboss.tools.forge.ui.part.ForgeView;
import org.jboss.tools.forge.ui.util.ForgeHelper;


public class NewProjectPostProcessor implements ForgeCommandPostProcessor {
	
	private String makePlatformIndependent(String path) {
		int index = path.indexOf('/');
		return (index != -1) ? path.substring(index) : path;
	}

	@Override
	public void postProcess(Map<String, String> commandDetails) {
		String projectPath = makePlatformIndependent(commandDetails.get("cpn"));
		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		String workspacePath = makePlatformIndependent(workspaceRoot.getLocation().toString());
		if (workspacePath.equals(projectPath)) {
			if (MessageDialog.open(
					MessageDialog.QUESTION, 
					null, 
					"Project Import Failed", 
					"The Forge runtime created the project in the workspace root. " +
					"Such a project cannot be imported.\n" +
					"Do you want to remove the created artifacts?",
					SWT.NONE)) {
				String fileSeparator = System.getProperty("file.separator");
				String pomPath = projectPath + fileSeparator + "pom.xml";
				File pomFile = new File(pomPath);
				if (pomFile.exists()) {
					delete(pomFile);
				}
				String srcPath = projectPath + fileSeparator + "src";
				File srcDir = new File(srcPath);
				if (srcDir.exists()) {
					delete(srcDir);
				}
				resetRuntime();
			}
		} else {
			int index = projectPath.lastIndexOf('/');
			if (index != -1) {
				String projectDirName = projectPath.substring(index + 1);
				String projectBaseDirPath = projectPath.substring(0, index);
				ProjectImporter importer = new ProjectImporter(projectBaseDirPath, projectDirName);
				importer.importProject();
			}
		}
	}
	
	private void delete(File f) {
		if (f.isDirectory()) {
			for (String s : f.list()) {
				delete(new File(f, s));
			}
		}
		f.delete();
	}
	
	private void resetRuntime() {
		ForgeView forgeView = ForgeHelper.getForgeView();
		if (forgeView != null) {
			ForgeRuntime runtime = ForgeRuntimesPreferences.INSTANCE.getDefaultRuntime();
			if (runtime != null && ForgeRuntime.STATE_RUNNING.equals(runtime.getState())) {
				runtime.sendInput("reset\n");
			}
		}
	}

}
