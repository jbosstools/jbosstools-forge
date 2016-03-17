/**
 * Copyright (c) Red Hat, Inc., contributors and others 2013 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.forge.ui.internal.ext.importer;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.maven.model.Model;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.project.IProjectConfigurationManager;
import org.eclipse.m2e.core.project.MavenProjectInfo;
import org.eclipse.m2e.core.project.ProjectImportConfiguration;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.IOverwriteQuery;
import org.eclipse.ui.part.ISetSelectionTarget;
import org.eclipse.ui.wizards.datatransfer.FileSystemStructureProvider;
import org.eclipse.ui.wizards.datatransfer.ImportOperation;
import org.jboss.tools.forge.ui.internal.ForgeUIPlugin;

/**
 * Imports a maven-ized project into the workspace
 *
 * FOR INTERNAL USE ONLY. This class was copied from the
 * org.jboss.tools.forge.ui plugin in order to avoid dependency on it and should
 * be removed in future versions.
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
public class ProjectImporter {

	private String baseDirPath;
	private String moduleLocation;
	private String projectName;

	public ProjectImporter(String baseDirPath, String moduleLocation,
			String projectName) {
		this.baseDirPath = baseDirPath;
		this.moduleLocation = moduleLocation;
		this.projectName = projectName;
	}

	public void importProject() {
		final Job job;
		File projectDir = new File(baseDirPath, moduleLocation);
		File pomFile = new File(projectDir, "pom.xml");
		String name = "Importing project " + projectName;
		if (pomFile.isFile()) {
			job = new MavenImportWorkspaceJob(name);
		} else {
			job = new GeneralImportWorkspaceJob(name);
		}
		job.setUser(true);
		job.schedule();
		try {
			job.join();
		} catch (InterruptedException e) {
			ForgeUIPlugin.log(e);
		}
		// Select created project
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				selectCreatedProject();
			}
		});

	}

	private Collection<MavenProjectInfo> getProjectToImport() {
		ArrayList<MavenProjectInfo> result = new ArrayList<>(1);
		result.add(createMavenProjectInfo());
		return result;
	}

	private MavenProjectInfo createMavenProjectInfo() {
		MavenProjectInfo result = null;
		try {
			File projectDir = new File(baseDirPath, moduleLocation);
			File pomFile = new File(projectDir, "pom.xml");
			Model model = MavenPlugin.getMavenModelManager().readMavenModel(
					pomFile);
			String pomName = moduleLocation + "/" + "pom.xml";
			result = new MavenProjectInfo(pomName, pomFile, model, null);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return result;
	}

	private void selectCreatedProject() {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = root.getProject(moduleLocation);
		if (project != null) {
			IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow();
			IWorkbenchPage workbenchPage = workbenchWindow.getActivePage();
			if (workbenchPage != null) {
				IViewPart projectExplorer = workbenchPage
						.findView("org.eclipse.ui.navigator.ProjectExplorer");
				if (projectExplorer instanceof ISetSelectionTarget) {
					((ISetSelectionTarget) projectExplorer)
							.selectReveal(new StructuredSelection(project));
				}
				IViewPart packageExplorer = workbenchPage
						.findView(JavaUI.ID_PACKAGES);
				if (packageExplorer == null && projectExplorer == null) {
					try {
						packageExplorer = workbenchPage
								.showView(JavaUI.ID_PACKAGES);
					} catch (PartInitException e) {
						ForgeUIPlugin.log(e);
					}
				}
				if (packageExplorer instanceof ISetSelectionTarget) {
					((ISetSelectionTarget) packageExplorer)
							.selectReveal(new StructuredSelection(project));
				}
			}
		}
	}

	private class GeneralImportWorkspaceJob extends WorkspaceJob {

		public GeneralImportWorkspaceJob(String name) {
			super(name);
		}

		@Override
		public IStatus runInWorkspace(IProgressMonitor monitor)
				throws CoreException {
			File projectDir = new File(baseDirPath, moduleLocation);
			IOverwriteQuery overwriteQuery = new IOverwriteQuery() {
				public String queryOverwrite(String file) {
					return ALL;
				}
			};
			IPath projectPath = Path.fromOSString(projectName);
			ImportOperation importOperation = new ImportOperation(projectPath,
					projectDir, FileSystemStructureProvider.INSTANCE,
					overwriteQuery);
			importOperation.setCreateContainerStructure(false);
			try {
				importOperation.run(monitor);
			} catch (Exception e) {
				return Status.CANCEL_STATUS;
			}
			return Status.OK_STATUS;
		}
	}

	private class MavenImportWorkspaceJob extends WorkspaceJob {

		public MavenImportWorkspaceJob(String name) {
			super(name);
			setRule(MavenPlugin.getProjectConfigurationManager().getRule());
		}

		@Override
		public IStatus runInWorkspace(IProgressMonitor monitor) {
			try {
				ProjectImportConfiguration config = new ProjectImportConfiguration();
				IProjectConfigurationManager configManager = MavenPlugin
						.getProjectConfigurationManager();
				Collection<MavenProjectInfo> projectToImport = getProjectToImport();
				configManager.importProjects(projectToImport, config, monitor);
			} catch (CoreException ex) {
				return ex.getStatus();
			}
			return Status.OK_STATUS;
		}
	}

}