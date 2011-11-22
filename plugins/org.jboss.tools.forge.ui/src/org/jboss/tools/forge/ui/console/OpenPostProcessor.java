package org.jboss.tools.forge.ui.console;

import java.io.File;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.part.ISetSelectionTarget;
import org.jboss.tools.forge.ui.ForgeUIPlugin;

public class OpenPostProcessor {
	
	public void postProcess(String line, String currentResourcePath) {
		String resourceToOpen = currentResourcePath + File.separator + line.substring(5).trim();
		IPath path = new Path(resourceToOpen);
		IFileStore fileStore = EFS.getLocalFileSystem().getStore(new Path(resourceToOpen));
		if (fileStore.fetchInfo().isDirectory()) {
			expandDirectoryInExplorer(path);
		} else {
			openFileInEditor(fileStore);
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
	
	private void expandDirectoryInExplorer(IPath path) {
		IWorkbenchPage workbenchPage = ForgeCommandPostProcessorHelper.getActiveWorkbenchPage();
		IContainer objectToSelect = ResourcesPlugin.getWorkspace().getRoot().getContainerForLocation(path);
		if (objectToSelect != null && workbenchPage != null) {
			IViewPart projectExplorer = workbenchPage.findView("org.eclipse.ui.navigator.ProjectExplorer");
			if (projectExplorer != null && projectExplorer instanceof CommonNavigator) {
				expandInProjectExplorer((CommonNavigator)projectExplorer, objectToSelect);
			} 
			IViewPart packageExplorer = workbenchPage.findView("org.eclipse.jdt.ui.PackageExplorer"); 
			if (packageExplorer == null && projectExplorer == null) {
				try {
					packageExplorer = workbenchPage.showView("org.eclipse.jdt.ui.PackageExplorer");
				} catch (PartInitException e) {
					ForgeUIPlugin.log(e);
				}
			} 
			if (packageExplorer != null) {
				expandInPackageExplorer(packageExplorer, objectToSelect);
			}
		}
	}
	
	private void expandInProjectExplorer(CommonNavigator projectExplorer, IContainer container) {
		projectExplorer.selectReveal(new StructuredSelection(container));
		TreeViewer treeViewer = projectExplorer.getCommonViewer();
		treeViewer.expandToLevel(container, 1);
	}
	
	private void expandInPackageExplorer(IViewPart packageExplorer, IContainer container) {
		if (packageExplorer instanceof ISetSelectionTarget) {
			((ISetSelectionTarget)packageExplorer).selectReveal(new StructuredSelection(container));
		}
		Object treeViewer = packageExplorer.getAdapter(ISelectionProvider.class);
		if (treeViewer != null && treeViewer instanceof TreeViewer) {
			((TreeViewer)treeViewer).expandToLevel(JavaCore.create(container), 1);
		}
	}
	
}
