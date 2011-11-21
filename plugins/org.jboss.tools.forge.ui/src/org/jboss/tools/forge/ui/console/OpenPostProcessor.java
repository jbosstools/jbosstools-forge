package org.jboss.tools.forge.ui.console;

import java.io.File;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.jboss.tools.forge.ui.ForgeUIPlugin;

public class OpenPostProcessor {
	
	public void postProcess(String line, String currentResourcePath) {
		try {
			String resourceToOpen = currentResourcePath + File.separator + line.substring(5).trim();
			IFileStore fileStore = EFS.getLocalFileSystem().getStore(new Path(resourceToOpen));
			if (fileStore.fetchInfo().isDirectory()) {
				expandDirectoryInExplorer(resourceToOpen);
			} else {
				openFileInEditor(fileStore);
			}
			IWorkbenchPage workbenchPage = ForgeCommandPostProcessorHelper.getActiveWorkbenchPage();
			if (workbenchPage != null) {
				IDE.openEditorOnFileStore(workbenchPage, fileStore);
			}
		} catch (PartInitException e) {
			ForgeUIPlugin.log(e);
		}
	}
	
	private void openFileInEditor(IFileStore fileStore) {
		try {
			IWorkbenchPage workbenchPage = ForgeCommandPostProcessorHelper.getActiveWorkbenchPage();
			if (workbenchPage != null) {
				IDE.openEditorOnFileStore(workbenchPage, fileStore);
			}
		} catch (PartInitException e) {
			ForgeUIPlugin.log(e);
		}
	}
	
	private void expandDirectoryInExplorer(String resourceToOpen) {
		
	}

}
