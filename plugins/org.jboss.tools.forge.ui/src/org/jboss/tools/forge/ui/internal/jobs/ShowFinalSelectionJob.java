/**
 * Copyright (c) Red Hat, Inc., contributors and others 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.forge.ui.internal.jobs;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Optional;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.BlockTextSelection;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.rse.ui.view.IRSEViewPart;
import org.eclipse.rse.ui.view.ISystemViewElementAdapter;
import org.eclipse.rse.ui.view.SystemAdapterHelpers;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.part.ISetSelectionTarget;
import org.eclipse.ui.texteditor.ITextEditor;
import org.jboss.forge.addon.ui.context.UIRegion;
import org.jboss.forge.addon.ui.context.UISelection;
import org.jboss.tools.forge.ui.internal.ForgeUIPlugin;
import org.jboss.tools.forge.ui.internal.ext.context.UIContextImpl;

public class ShowFinalSelectionJob extends ChainedWorkspaceJob {

	private UIContextImpl context;

	public ShowFinalSelectionJob() {
		super("Show Final Selection");
	}

	public void setContext(UIContextImpl context) {
		this.context = context;
	}

	@Override
	public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
		UISelection<Object> selection = context.getSelection();
		if (selection != null && !selection.isEmpty()) {
			selectResourceFor(selection.get(), selection.getRegion());
		}
		return Status.OK_STATUS;
	}

	private void selectResourceFor(Object object, Optional<UIRegion<Object>> region) {
		try {
			Method method = object.getClass().getMethod("getUnderlyingResourceObject", new Class[] {});
			if (method != null) {
				final Object resource = method.invoke(object, new Object[] {});
				if (resource != null && resource instanceof File) {
					Display.getDefault().asyncExec(() -> selectFile((File) resource, region));
				}
			}
		} catch (NoSuchMethodException e) {
			// ignore
		} catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			ForgeUIPlugin.log(e);
		}
	}

	private void selectFile(File file, Optional<UIRegion<Object>> region) {
		try {
			IPath path = new Path(file.getCanonicalPath());
			IFileStore fileStore = EFS.getLocalFileSystem().getStore(path);
			IFileInfo fileInfo = fileStore.fetchInfo();
			if (!fileInfo.exists())
				return;
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
				openFileInEditor(fileStore, region);
			}
		} catch (IOException e) {
			ForgeUIPlugin.log(e);
		}
	}

	private void expandWorkspaceResource(IResource container) {
		IWorkbenchPage workbenchPage = getActiveWorkbenchPage();
		if (workbenchPage != null) {
			refreshWorkspaceResource(container);
			IViewPart projectExplorer = workbenchPage.findView(IPageLayout.ID_PROJECT_EXPLORER);
			if (projectExplorer != null && projectExplorer instanceof CommonNavigator) {
				expandInProjectExplorer((CommonNavigator) projectExplorer, container);
			}
			IViewPart packageExplorer = workbenchPage.findView(JavaUI.ID_PACKAGES);
			if (packageExplorer != null) {
				expandInPackageExplorer(packageExplorer, container);
			}
		}
	}

	private void expandSystemDirectory(IFileStore fileStore) {
		IWorkbenchPage workbenchPage = getActiveWorkbenchPage();
		IViewPart remoteSystemView = workbenchPage.findView("org.eclipse.rse.ui.view.systemView");
		if (remoteSystemView != null) {
			expandInRemoteSystemView(remoteSystemView, fileStore);
		}
	}

	private void openFileInEditor(IFileStore fileStore, Optional<UIRegion<Object>> region) {
		try {
			IWorkbenchPage workbenchPage = getActiveWorkbenchPage();
			if (workbenchPage != null) {
				IEditorPart editorPart = IDE.openEditorOnFileStore(workbenchPage, fileStore);
				if (editorPart instanceof ITextEditor) {
					ITextEditor itextEditor = (ITextEditor) editorPart;
					region.ifPresent(r -> {
						IDocument doc = itextEditor.getDocumentProvider().getDocument(itextEditor.getEditorInput());
						// Line is 0-based, UIRegion.getStart/EndLine is not
						BlockTextSelection selection = new BlockTextSelection(doc, r.getStartLine() - 1,
								r.getStartPosition(), r.getEndLine() - 1, r.getEndPosition(), 1);

						ISelectionProvider selectionProvider = itextEditor.getSelectionProvider();
						selectionProvider.setSelection(selection);
					});
				}
			}
		} catch (PartInitException e) {
			ForgeUIPlugin.log(e);
		}
	}

	private IWorkbenchPage getActiveWorkbenchPage() {
		IWorkbenchPage result = null;
		IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (workbenchWindow != null) {
			result = workbenchWindow.getActivePage();
		}
		return result;
	}

	private void refreshWorkspaceResource(IResource container) {
		try {
			container.refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (CoreException e) {
			ForgeUIPlugin.log(e);
		}
	}

	private void expandInProjectExplorer(CommonNavigator projectExplorer, IResource container) {
		projectExplorer.selectReveal(new StructuredSelection(container));
		TreeViewer treeViewer = projectExplorer.getCommonViewer();
		treeViewer.expandToLevel(container, 1);
	}

	private void expandInPackageExplorer(IViewPart packageExplorer, IResource container) {
		if (packageExplorer instanceof ISetSelectionTarget) {
			((ISetSelectionTarget) packageExplorer).selectReveal(new StructuredSelection(container));
		}
		Object treeViewer = packageExplorer.getAdapter(ISelectionProvider.class);
		if (treeViewer != null && treeViewer instanceof TreeViewer) {
			((TreeViewer) treeViewer).expandToLevel(JavaCore.create(container), 1);
		}
	}

	private void expandInRemoteSystemView(IViewPart remoteSystemView, IFileStore fileStore) {
		Viewer viewer = getViewer(remoteSystemView);
		if (viewer == null)
			return;
		Object input = viewer.getInput();
		ArrayList<String> names = createSegmentNames(fileStore);
		ArrayList<Object> treeSegments = new ArrayList<>();
		for (String name : names) {
			if (input instanceof IAdaptable) {
				ISystemViewElementAdapter adapter = SystemAdapterHelpers.getViewAdapter(input);
				if (adapter != null) {
					for (Object object : adapter.getChildren((IAdaptable) input, null)) {
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
			((TreeViewer) viewer).expandToLevel(treePath, 1);

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
		ArrayList<String> result = new ArrayList<>();
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

}
