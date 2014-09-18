/**
 * Copyright (c) Red Hat, Inc., contributors and others 2013 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.forge.ui.internal.cli;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.rse.ui.view.IRSEViewPart;
import org.eclipse.rse.ui.view.ISystemViewElementAdapter;
import org.eclipse.rse.ui.view.SystemAdapterHelpers;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.part.ISetSelectionTarget;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectListener;
import org.jboss.forge.addon.projects.facets.MetadataFacet;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.ui.command.CommandExecutionListener;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UISelection;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.tools.forge.core.util.ProjectTools;
import org.jboss.tools.forge.ui.internal.ForgeUIPlugin;
import org.jboss.tools.forge.ui.internal.part.ForgeConsoleView;
import org.jboss.tools.forge.ui.internal.util.IDEUtils;

public class CommandLineListener implements ProjectListener, CommandExecutionListener {
	
	private List<Project> projects = new ArrayList<Project>();

	@Override
	public void postCommandExecuted(
			UICommand command, 
			final UIExecutionContext uiExecutionContext,
			Result result) {
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				if (!projects.isEmpty()) {
					importProjects();
					projects.clear();
				}
				UISelection<?> selection = uiExecutionContext.getUIContext().getInitialSelection();
				Iterator<?> iterator = selection.iterator();
				while (iterator.hasNext()) {
					Object object = iterator.next();
					if (object instanceof Resource<?>) {
						refresh((Resource<?>)object);
					}
				}
				if (pomFileModificationStamp != -1 && pomFile != null && pomFile.getModificationStamp() > pomFileModificationStamp) {
					ProjectTools.updateProjectConfiguration(pomFile.getProject());
				}
				selection = uiExecutionContext.getUIContext().getSelection();
				if (selection != null && !selection.isEmpty()) {
					Object resource = selection.get();
					if (resource instanceof Resource<?>) {
						selectResource((Resource<?>)resource);
					}
				}
				activateForgeView();
				pomFile = null;
				pomFileModificationStamp = -1;
			}			
		});
	}
	
	private void activateForgeView() {
		try {
			IWorkbenchPage activePage = getActivePage();
			if (activePage != null) {
				IViewPart part = activePage.showView(ForgeConsoleView.ID);
				activePage.activate(part);
			}
		} catch (PartInitException e) {
			ForgeUIPlugin.log(e);
		}
	}
	
	private IWorkbenchPage getActivePage() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
	}
	
	private void refresh(Resource<?> resource) {
		Object object = resource.getUnderlyingResourceObject();
		if (object instanceof File) {
			refreshResource((File)object);
		}
	}
	
	private void selectResource(Resource<?> resource) {
		Object object = resource.getUnderlyingResourceObject();
		if (object instanceof File) {
			selectFile((File)object);
		}
	}
	
	@Override
	public void projectCreated(Project project) {
		projects.add(project);
	}

	private void importProjects() {
		for (Project project : projects) {
			Resource<?> projectRoot = project.getRoot();
			String baseDirPath = projectRoot.getParent()
					.getFullyQualifiedName();
			String projectName = project.getFacet(MetadataFacet.class)
					.getProjectName();
			ProjectTools.importProject(baseDirPath, projectName);
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
				IDEUtils.openFileInEditor(fileStore, false);
			}
			if (resource != null) {
				expandWorkspaceResource(resource);
			} else {
				expandSystemDirectory(fileStore);
			}
		} catch (IOException e) {
			ForgeUIPlugin.log(e);
		}
	}
	
	private void expandWorkspaceResource(IResource container) {
		IWorkbenchPage workbenchPage = getActiveWorkbenchPage();
		if (workbenchPage != null) {
			IViewPart projectExplorer = workbenchPage.findView(IPageLayout.ID_PROJECT_EXPLORER);
			if (projectExplorer != null && projectExplorer instanceof CommonNavigator) {
				expandInProjectExplorer((CommonNavigator)projectExplorer, container);
			} 
			IViewPart packageExplorer = workbenchPage.findView(JavaUI.ID_PACKAGES); 
			if (packageExplorer != null) {
				expandInPackageExplorer(packageExplorer, container);
			}
		}
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
	

 	private void refreshResource(File file) {
		try {
			IPath path = new Path(file.getCanonicalPath());
			IFileStore fileStore = EFS.getLocalFileSystem().getStore(path);
			IFileInfo fileInfo = fileStore.fetchInfo();
			if (!fileInfo.exists()) return;
			if (fileInfo.isDirectory()) {
				IContainer container = ResourcesPlugin.getWorkspace().getRoot().getContainerForLocation(path);
				if (container != null) {
					container.refreshLocal(IResource.DEPTH_INFINITE, null);
				}
			} else {
				IResource resource = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(path);
				if (resource != null) {
					resource.refreshLocal(IResource.DEPTH_INFINITE, null);
				}
			}
		} catch (IOException e) {
			ForgeUIPlugin.log(e);
		} catch (CoreException e) {
			ForgeUIPlugin.log(e);
		}
		try {
			ResourcesPlugin.getWorkspace().getRoot().refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (CoreException e) {
			ForgeUIPlugin.log(e);
		}
	}

	@Override
	public void postCommandFailure(UICommand arg0, UIExecutionContext arg1,
			Throwable arg2) {
		pomFile = null;
		pomFileModificationStamp = -1;
	}
	
	private IFile pomFile = null;
	private long pomFileModificationStamp = -1;

	@Override
	public void preCommandExecuted(UICommand command, UIExecutionContext executionContext) {
		UISelection<?> selection = executionContext.getUIContext().getInitialSelection();
		Iterator<?> iterator = selection.iterator();
		while (iterator.hasNext()) {
			Object object = iterator.next();
			if (object instanceof Resource<?>) {
				pomFile = determinePomFile((Resource<?>)object);
				if (pomFile != null) {
					pomFileModificationStamp = pomFile.getModificationStamp();
				}
			}
		}
	}
	
	private IFile determinePomFile(Resource<?> resource) {
		IFile result = null;
		try {
			Object object = resource.getUnderlyingResourceObject();
			if (object != null && object instanceof File) {
				Path path = new Path(((File)object).getCanonicalPath());
				IFileStore fileStore = EFS.getLocalFileSystem().getStore(path);
				IFileInfo fileInfo = fileStore.fetchInfo();
				if (fileInfo.exists()) {
					IResource res = null;
					if (fileInfo.isDirectory()) {
						res = ResourcesPlugin.getWorkspace().getRoot().getContainerForLocation(path);
					} else {
						res = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(path);
					}
					if (res != null) {
						IProject project = res.getProject();
						if (project != null) {
							result = project.getFile(new Path("pom.xml"));
						}
					}
				}
			}
		} catch (IOException e) {
			ForgeUIPlugin.log(e);
		}
		return result;
	}
	
	
	
}
