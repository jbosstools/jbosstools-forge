package org.jboss.tools.forge.ui.console;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class ForgeCommandPostProcessorHelper {

	public static String getProjectName(String command) {
		String result = null;
		int i = command.indexOf("Current Project Directory: ");
		if (i != -1 && command.length() >= i + 27) {
			result = command.substring(i + 27).trim();
			if ("".equals(result)) {
				result = null;
			}
		}
		return result;
	}
	
	public static IProject getProject(String command) {
		IProject result = null;
		String projectName = getProjectName(command);
		if (projectName != null) {
			result = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		}
		return result;
	}
	
	public static IWorkbenchPage getActiveWorkbenchPage() {
		IWorkbenchPage result = null;
		IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (workbenchWindow != null) {
			result = workbenchWindow.getActivePage();
		}
		return result;		
	}
	
}
