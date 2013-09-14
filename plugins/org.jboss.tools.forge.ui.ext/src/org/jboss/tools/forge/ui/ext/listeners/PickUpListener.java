package org.jboss.tools.forge.ui.ext.listeners;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.rse.ui.view.IRSEViewPart;
import org.eclipse.rse.ui.view.ISystemViewElementAdapter;
import org.eclipse.rse.ui.view.SystemAdapterHelpers;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.part.ISetSelectionTarget;
import org.jboss.tools.forge.ui.ext.ForgeUIPlugin;
import org.jboss.tools.forge.ui.ext.context.UIContextImpl;
import org.jboss.tools.forge.ui.ext.wizards.WizardListener;

public enum PickUpListener implements WizardListener {

	INSTANCE;

	@Override
	public void dispose() {
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public void onFinish(UIContextImpl context) {
		Object selection = context.getSelection();
		if (selection != null) {
			if (selection instanceof Iterable<?>) {
				for (Object item : (Iterable<Object>) selection) {
					if (item != null)
						open(item.toString());
				}
			} else if (selection instanceof Iterator<?>) {
				Iterator<Object> it = (Iterator<Object>) selection;
				while (it.hasNext()) {
					Object item = it.next();
					if (item != null)
						open(item.toString());
				}
			} else if (selection.getClass().isArray()) {
				int length = Array.getLength(selection);
				for (int i = 0; i < length; i++) {
					Object item = Array.get(selection, i);
					if (item != null) {
						open(item.toString());
					}
				}
			} else {
				open(selection.toString());
			}
		}
	}

	public void open(String resource) {
		if (resource == null)
			return;
		IPath path = new Path(resource);
		IFileStore fileStore = EFS.getLocalFileSystem().getStore(path);
		IFileInfo fileInfo = fileStore.fetchInfo();
		if (!fileInfo.exists())
			return;
		if (fileInfo.isDirectory()) {
			IContainer container = ResourcesPlugin.getWorkspace().getRoot()
					.getContainerForLocation(path);
			if (container != null) {
				expandWorkspaceDirectory(container);
			} else {
				expandSystemDirectory(fileStore);
			}
		} else {
			openFileInEditor(fileStore);
		}
	}

	private static IWorkbenchPage getActiveWorkbenchPage() {
		IWorkbenchPage result = null;
		IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		if (workbenchWindow != null) {
			result = workbenchWindow.getActivePage();
		}
		return result;
	}

	private void expandSystemDirectory(IFileStore fileStore) {
		IWorkbenchPage workbenchPage = getActiveWorkbenchPage();
		if (workbenchPage != null) {
			IViewPart remoteSystemView = workbenchPage
					.findView("org.eclipse.rse.ui.view.systemView");
			if (remoteSystemView == null) {
				try {
					remoteSystemView = workbenchPage
							.showView("org.eclipse.rse.ui.view.systemView");
				} catch (PartInitException e) {
					ForgeUIPlugin.log(e);
				}
			}
			if (remoteSystemView != null) {
				expandInRemoteSystemView(remoteSystemView, fileStore);
			}
		}
	}

	private void expandWorkspaceDirectory(IContainer container) {
		IWorkbenchPage workbenchPage = getActiveWorkbenchPage();
		if (workbenchPage != null) {
			IViewPart projectExplorer = workbenchPage
					.findView("org.eclipse.ui.navigator.ProjectExplorer");
			if (projectExplorer != null
					&& projectExplorer instanceof CommonNavigator) {
				expandInProjectExplorer((CommonNavigator) projectExplorer,
						container);
			}
			IViewPart packageExplorer = workbenchPage
					.findView("org.eclipse.jdt.ui.PackageExplorer");
			if (packageExplorer == null && projectExplorer == null) {
				try {
					packageExplorer = workbenchPage
							.showView("org.eclipse.jdt.ui.PackageExplorer");
				} catch (PartInitException e) {
					ForgeUIPlugin.log(e);
				}
			}
			if (packageExplorer != null) {
				expandInPackageExplorer(packageExplorer, container);
			}
		}
	}

	private void openFileInEditor(IFileStore fileStore) {
		try {
			IWorkbenchPage workbenchPage = getActiveWorkbenchPage();
			if (workbenchPage != null) {
				IDE.openEditorOnFileStore(workbenchPage, fileStore);
			}
		} catch (PartInitException e) {
			ForgeUIPlugin.log(e);
		}
	}

	private void expandInProjectExplorer(CommonNavigator projectExplorer,
			IContainer container) {
		projectExplorer.selectReveal(new StructuredSelection(container));
		TreeViewer treeViewer = projectExplorer.getCommonViewer();
		treeViewer.expandToLevel(container, 1);
	}

	private void expandInPackageExplorer(IViewPart packageExplorer,
			IContainer container) {
		if (packageExplorer instanceof ISetSelectionTarget) {
			((ISetSelectionTarget) packageExplorer)
					.selectReveal(new StructuredSelection(container));
		}
		Object treeViewer = packageExplorer
				.getAdapter(ISelectionProvider.class);
		if (treeViewer != null && treeViewer instanceof TreeViewer) {
			((TreeViewer) treeViewer).expandToLevel(JavaCore.create(container),
					1);
		}
	}

	private Viewer getViewer(IViewPart remoteSystemView) {
		if (remoteSystemView instanceof IRSEViewPart) {
			return ((IRSEViewPart) remoteSystemView).getRSEViewer();
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

	private void expandInRemoteSystemView(IViewPart remoteSystemView,
			IFileStore fileStore) {
		Viewer viewer = getViewer(remoteSystemView);
		Object input = viewer.getInput();
		ArrayList<String> names = createSegmentNames(fileStore);
		ArrayList<Object> treeSegments = new ArrayList<Object>();
		for (String name : names) {
			if (input != null && input instanceof IAdaptable) {
				ISystemViewElementAdapter adapter = SystemAdapterHelpers
						.getViewAdapter(input);
				if (adapter != null) {
					for (Object object : adapter.getChildren(
							(IAdaptable) input, null)) {
						if (object instanceof IAdaptable) {
							adapter = SystemAdapterHelpers
									.getViewAdapter(object);
							if (adapter != null
									&& name.equals(adapter.getText(object))) {
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
			((TreeViewer) viewer).expandToLevel(treePath, 1);
		}
	}
}
