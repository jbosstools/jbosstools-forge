package org.jboss.tools.forge.ui.console;

import java.util.ArrayList;
import java.util.Map;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.rse.ui.view.IRSEViewPart;
import org.eclipse.rse.ui.view.ISystemViewElementAdapter;
import org.eclipse.rse.ui.view.SystemAdapterHelpers;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ISetSelectionTarget;
import org.jboss.tools.forge.ui.ForgeUIPlugin;


public class CdPostProcessor implements ForgeCommandPostProcessor {
	
	protected String getResourceToShow(Map<String, String> commandDetails) {
		return commandDetails.get("crn");
	}
	
	@Override
	public void postProcess(Map<String, String> commandDetails) {
		String crn = getResourceToShow(commandDetails);
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
			show(resource);
		}
		show(fileStore);
	}
	
	private void show(IFileStore fileStore) {
		IWorkbenchPage workbenchPage = ForgeCommandPostProcessorHelper.getActiveWorkbenchPage();
		IViewPart remoteSystemView = workbenchPage.findView("org.eclipse.rse.ui.view.systemView");
		if (remoteSystemView != null) {
			expandInRemoteSystemView(remoteSystemView, fileStore);
		}
	}
	
	private void show(IResource resource) {
		IWorkbenchPage workbenchPage = ForgeCommandPostProcessorHelper.getActiveWorkbenchPage();
		if (workbenchPage != null) {
			IViewPart projectExplorer = workbenchPage.findView("org.eclipse.ui.navigator.ProjectExplorer");
			if (projectExplorer != null) {
				show(projectExplorer, resource);
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
				show(packageExplorer, resource);
			}
		}
	}
	
	private void show(IViewPart explorer, IResource resource) {
		if (explorer instanceof ISetSelectionTarget) {
			((ISetSelectionTarget)explorer).selectReveal(new StructuredSelection(resource));
		}
	}
	
	private Viewer getViewer(IViewPart remoteSystemView) {
		if (remoteSystemView instanceof IRSEViewPart) {
			return ((IRSEViewPart)remoteSystemView).getRSEViewer();
		} else {
			return null;
		}
	}
	
	private ArrayList<String> createSegmentNames(IFileStore fileStore) {
		ArrayList<String> result = new ArrayList<String>();
		while (fileStore.getParent() != null) {
			result.add(0, fileStore.getName());
			fileStore = fileStore.getParent();
		}
		result.add(0, "/");
		result.add(0, "Root");
		result.add(0, "Local Files");
		result.add(0, "Local");
		return result;
	}
	
	private void expandInRemoteSystemView(
			IViewPart remoteSystemView, 
			IFileStore fileStore) {
		Viewer viewer = getViewer(remoteSystemView);
		Object input = viewer.getInput();
		ArrayList<String> names = createSegmentNames(fileStore);
		ArrayList<Object> treeSegments = new ArrayList<Object>();
		for (String name : names) {
			if (input != null && input instanceof IAdaptable) {
				ISystemViewElementAdapter adapter = SystemAdapterHelpers.getViewAdapter(input);
				if (adapter != null) {
					for (Object object : adapter.getChildren((IAdaptable)input, null)) {
						if (object instanceof IAdaptable) {
							adapter = SystemAdapterHelpers.getViewAdapter(object);
							if (adapter != null && name.equals(adapter.getText(object))) {
								input = object;
								treeSegments.add(input);
								break;
							}
						}
					}
				}
			} else {
				treeSegments.clear();
				break;
			}
		}
		TreePath treePath = new TreePath(treeSegments.toArray());
		viewer.setSelection(new StructuredSelection(treePath));
		if (viewer instanceof TreeViewer) {
			((TreeViewer)viewer).expandToLevel(treePath, 1);
			
		}
	}
}
