package org.jboss.tools.forge.ui.console;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.jboss.tools.forge.ui.ForgeUIPlugin;


public class RmPostProcessor implements ForgeCommandPostProcessor {
	
	private ArrayList<String> getResourcesToDelete(Map<String, String> commandDetails) {
		String crn = commandDetails.get("crn");
		String par = commandDetails.get("par");
		int start = par.lastIndexOf('[');
		int end = par.lastIndexOf(']');
		if (start == -1 || end == -1) return null;
		par = par.substring(start + 1, end);
		ArrayList<String> result = new ArrayList<String>();
		StringTokenizer tokenizer = new StringTokenizer(par);
		while (tokenizer.hasMoreTokens()) {
			result.add(crn + File.separator + tokenizer.nextToken());
		}
		return result;
	}
	
	@Override
	public void postProcess(Map<String, String> commandDetails) {
		ArrayList<String> resourceNames = getResourcesToDelete(commandDetails);
		if (resourceNames == null) return;
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for (String resourceName : resourceNames) {
			if (resourceName.endsWith("/")) {
				resourceName = resourceName.substring(0, resourceName.length() - 1);
			}
			for (IProject project : projects) {
				if (project.exists() && resourceName.equals(project.getLocation().toOSString())) {
					try {
						project.delete(false, false, null);
					} catch (CoreException e) {
						ForgeUIPlugin.log(e);
					}
				}
			}
		}
	}
	
}
