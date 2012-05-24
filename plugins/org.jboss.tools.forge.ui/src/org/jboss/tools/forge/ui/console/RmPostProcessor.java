package org.jboss.tools.forge.ui.console;

import java.util.Map;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.jboss.tools.forge.ui.ForgeUIPlugin;


public class RmPostProcessor implements ForgeCommandPostProcessor {
	
	private String getResourceToDelete(Map<String, String> commandDetails) {
		return commandDetails.get("crn");
	}
	
	@Override
	public void postProcess(Map<String, String> commandDetails) {
		String crn = getResourceToDelete(commandDetails);
		if (crn == null) return;
		IPath path = new Path(crn);
		IFileStore fileStore = EFS.getLocalFileSystem().getStore(new Path(crn));
		IFileInfo fileInfo = fileStore.fetchInfo();
		if (!fileInfo.exists()) return;
		IResource resource = null;
		if (fileInfo.isDirectory()) {
			resource = ResourcesPlugin.getWorkspace().getRoot().getContainerForLocation(path);
		} else {
			resource = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(path);
		}
		if (resource != null) {
			delete(resource);
		}
	}
	
	private void delete(IResource resource) {
		try { 
			resource.delete(true, null);
		} catch (CoreException e) {
			ForgeUIPlugin.log(e);
		}
	}
	
}
