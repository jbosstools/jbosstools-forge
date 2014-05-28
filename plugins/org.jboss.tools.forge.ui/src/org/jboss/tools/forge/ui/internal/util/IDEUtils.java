package org.jboss.tools.forge.ui.internal.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.jboss.tools.forge.ui.internal.ForgeUIPlugin;

public class IDEUtils {

	public static void openFileInEditor(IFileStore fileStore, boolean activate) {
		try {
			IWorkbenchPage workbenchPage = getActiveWorkbenchPage();
			if (workbenchPage != null) {
				IEditorInput editorInput = getEditorInput(fileStore);
				String editorId = getEditorId(fileStore);
				IDE.openEditor(workbenchPage, editorInput, editorId, activate);
			}
		} catch (PartInitException e) {
			ForgeUIPlugin.log(e);
		}
	}

	private static IWorkbenchPage getActiveWorkbenchPage() {
		IWorkbenchPage result = null;
		IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (workbenchWindow != null) {
			result = workbenchWindow.getActivePage();
		}
		if (result != null) {
			
		}
		return result;		
	}

 	private static IEditorInput getEditorInput(IFileStore fileStore) {
 		IFile workspaceFile = getWorkspaceFile(fileStore);
 		if (workspaceFile != null)
 			return new FileEditorInput(workspaceFile);
 		return new FileStoreEditorInput(fileStore);
 	}

	private static IFile getWorkspaceFile(IFileStore fileStore) {
 		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
 		IFile[] files = root.findFilesForLocationURI(fileStore.toURI());
 		files = filterNonExistentFiles(files);
 		if (files == null || files.length == 0)
 			return null; 
 		return files[0];
 	}
	
	private static IFile[] filterNonExistentFiles(IFile[] files) {
 		if (files == null) return null;
 		int length = files.length;
 		ArrayList<IFile> existentFiles = new ArrayList<IFile>(length);
 		for (int i = 0; i < length; i++) {
 			if (files[i].exists())
 				existentFiles.add(files[i]);
 		}
 		return existentFiles.toArray(new IFile[existentFiles.size()]);
 	}
	
 	private static String getEditorId(IFileStore fileStore) throws PartInitException {
 		String name = fileStore.fetchInfo().getName();
 		IContentType contentType = null;
    	try {
 			InputStream is = null;
 			try {
 				is = fileStore.openInputStream(EFS.NONE, null);
				contentType = Platform.getContentTypeManager().findContentTypeFor(is, name);
 			} finally {
				if (is != null) {
 					is.close();
 				}
 			}
 		} catch (CoreException ex) {
 			// continue without content type
 		} catch (IOException ex) {
 			// continue without content type
 		}
 		IEditorRegistry editorReg = PlatformUI.getWorkbench().getEditorRegistry(); 
 		IEditorDescriptor defaultEditor = editorReg.getDefaultEditor(name, contentType);
 		defaultEditor = IDE.overrideDefaultEditorAssociation(new FileStoreEditorInput(fileStore), contentType, defaultEditor);
 		return getEditorDescriptor(name, editorReg, defaultEditor).getId();
 	}


 	private static IEditorDescriptor getEditorDescriptor(
 				String name,
 				IEditorRegistry editorReg, 
				IEditorDescriptor defaultDescriptor)
						throws PartInitException { 
 		if (defaultDescriptor != null) {
 			return defaultDescriptor;
 		} 
 		IEditorDescriptor editorDesc = defaultDescriptor;
 		if (editorReg.isSystemInPlaceEditorAvailable(name)) {
 			editorDesc = editorReg
 					.findEditor(IEditorRegistry.SYSTEM_INPLACE_EDITOR_ID);
 		}
 		if (editorDesc == null
 				&& editorReg.isSystemExternalEditorAvailable(name)) {
 			editorDesc = editorReg
 					.findEditor(IEditorRegistry.SYSTEM_EXTERNAL_EDITOR_ID);
 		}
 		if (editorDesc == null) {
			editorDesc = editorReg
 					.findEditor("org.eclipse.ui.DefaultTextEditor");
 		}
 		if (editorDesc == null) {
 			throw new PartInitException(
 					"An appropriate editor could not be found");
 		} 
 		return editorDesc;
 	}

}
