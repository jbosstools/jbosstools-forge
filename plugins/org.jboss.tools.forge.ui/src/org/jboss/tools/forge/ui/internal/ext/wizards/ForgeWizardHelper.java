/**
 * Copyright (c) Red Hat, Inc., contributors and others 2013 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.forge.ui.internal.ext.wizards;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.part.ISetSelectionTarget;
import org.jboss.tools.forge.core.util.ProjectTools;
import org.jboss.tools.forge.ui.internal.ForgeUIPlugin;
import org.jboss.tools.forge.ui.internal.ext.context.UIContextImpl;
import org.jboss.tools.forge.ui.internal.ext.context.UISelectionImpl;
import org.jboss.tools.forge.ui.internal.ext.importer.ImportEclipseProjectListener;

public class ForgeWizardHelper {

	private IFile pomFile = null;
	private long pomFileModificationStamp = -1;

	public void onFinish(final UIContextImpl context) {
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				importNewProjects();
			}			
		});
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				refreshInitialSelection(context);
			}			
		});
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				updateProjectConfiguration();
			}			
		});
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				showFinalSelection(context);
			}			
		});
	}
	
	public void onCancel(UIContextImpl context) {
		pomFile = null;
		pomFileModificationStamp = -1;
	}
	
	public void onCreate(UIContextImpl context) {
		UISelectionImpl<?> selection = context.getInitialSelection();
		if (selection != null) {
			IResource resource = selection.getResource();
			if (resource != null) {
				pomFile = determinePomFile(selection.getResource());
				if (pomFile != null) {
					pomFileModificationStamp = pomFile.getModificationStamp();
				}
			}
		}
	}
	
	private void importNewProjects() {
		if (ImportEclipseProjectListener.INSTANCE.projectsAvailableForImport()) {
			ImportEclipseProjectListener.INSTANCE.doImport();
		}
	}
	
	private void refreshInitialSelection(UIContextImpl context) {
		try {
			UISelectionImpl<?> selection = context.getInitialSelection();
			if (selection != null) {
				IResource resource = selection.getResource();
				if (resource != null) {
					if (resource.isPhantom()) {
						// resource was deleted
						resource = resource.getParent();
					}
					if (resource != null) {
						resource.refreshLocal(IResource.DEPTH_INFINITE, null);
					}
				}
			}
		} catch (CoreException e) {
			ForgeUIPlugin.log(e);
		}
	}
	
	private void updateProjectConfiguration() {
		if (pomFileModificationStamp != -1 && pomFile != null && pomFile.getModificationStamp() > pomFileModificationStamp) {
			ProjectTools.updateProjectConfiguration(pomFile.getProject());
		}
		pomFile = null;
		pomFileModificationStamp = -1;
	}
	
	private void showFinalSelection(UIContextImpl context) {
		Object object = context.getSelection();
		if (object != null) {
			selectResourceFor(object);
		}		
	}
	
	private void selectResourceFor(Object object) {
		try {
			Method method = object.getClass().getMethod(
					"getUnderlyingResourceObject", 
					new Class[] {});
			if (method != null) {
				Object resource = method.invoke(object, new Object[] {});
				if (resource != null && resource instanceof File) {
					selectFile((File)resource);
				}
			}
		} catch (NoSuchMethodException e) {
			// ignore
		} catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			ForgeUIPlugin.log(e);
		}
	}

	private void selectFile(File file) {
		try {
			IPath path = new Path(file.getCanonicalPath());
			IFileStore fileStore = EFS.getLocalFileSystem().getStore(path);
			IFileInfo fileInfo = fileStore.fetchInfo();
			if (!fileInfo.exists()) return;
			IResource resource = null;
			if (fileInfo.isDirectory()) {
				resource = ResourcesPlugin.getWorkspace().getRoot().getContainerForLocation(path);
			} else {
				resource = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(path);
			}
			if (resource != null) {
				expandWorkspaceResource(resource);
			} else {
				expandSystemDirectory(fileStore);
			}
			if (!fileInfo.isDirectory()) {
				openFileInEditor(fileStore);
			}
		} catch (IOException e) {
			ForgeUIPlugin.log(e);
		}
	}
	
	private void expandWorkspaceResource(IResource container) {
		IWorkbenchPage workbenchPage = getActiveWorkbenchPage();
		if (workbenchPage != null) {
			refreshWorkspaceResource(container);
			IViewPart projectExplorer = workbenchPage.findView("org.eclipse.ui.navigator.ProjectExplorer");
			if (projectExplorer != null && projectExplorer instanceof CommonNavigator) {
				expandInProjectExplorer((CommonNavigator)projectExplorer, container);
			} 
			IViewPart packageExplorer = workbenchPage.findView("org.eclipse.jdt.ui.PackageExplorer"); 
			if (packageExplorer != null) {
				expandInPackageExplorer(packageExplorer, container);
			}
		}
	}
	
	private void refreshWorkspaceResource(IResource container) {
		try {
			container.refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (CoreException e) {
			ForgeUIPlugin.log(e);
		}
	}
	
	private void expandSystemDirectory(IFileStore fileStore) {
		IWorkbenchPage workbenchPage = getActiveWorkbenchPage();
		IViewPart remoteSystemView = workbenchPage.findView("org.eclipse.rse.ui.view.systemView");
		if (remoteSystemView != null) {
			expandInRemoteSystemView(remoteSystemView, fileStore);
		}
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
	
	private IWorkbenchPage getActiveWorkbenchPage() {
		IWorkbenchPage result = null;
		IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (workbenchWindow != null) {
			result = workbenchWindow.getActivePage();
		}
		if (result != null) {
			
		}
		return result;		
	}

	private void expandInProjectExplorer(CommonNavigator projectExplorer, IResource container) {
		projectExplorer.selectReveal(new StructuredSelection(container));
		TreeViewer treeViewer = projectExplorer.getCommonViewer();
		treeViewer.expandToLevel(container, 1);
	}
	
	private void expandInPackageExplorer(IViewPart packageExplorer, IResource container) {
		if (packageExplorer instanceof ISetSelectionTarget) {
			((ISetSelectionTarget)packageExplorer).selectReveal(new StructuredSelection(container));
		}
		Object treeViewer = packageExplorer.getAdapter(ISelectionProvider.class);
		if (treeViewer != null && treeViewer instanceof TreeViewer) {
			((TreeViewer)treeViewer).expandToLevel(JavaCore.create(container), 1);
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

	private IFile determinePomFile(IResource resource) {
		IFile result = null;
		IProject project = resource.getProject();
		if (project != null) {
			result = project.getFile(new Path("pom.xml"));
		}
		return result;
	}
	
}
