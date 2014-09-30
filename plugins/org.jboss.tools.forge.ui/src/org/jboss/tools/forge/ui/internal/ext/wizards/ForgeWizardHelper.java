/**
 * Copyright (c) Red Hat, Inc., contributors and others 2013 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.forge.ui.internal.ext.wizards;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Path;
import org.jboss.tools.forge.ui.internal.ext.context.UIContextImpl;
import org.jboss.tools.forge.ui.internal.ext.context.UISelectionImpl;
import org.jboss.tools.forge.ui.internal.jobs.ImportNewProjectsJob;
import org.jboss.tools.forge.ui.internal.jobs.RefreshInitialSelectionJob;
import org.jboss.tools.forge.ui.internal.jobs.ShowFinalSelectionJob;
import org.jboss.tools.forge.ui.internal.jobs.UpdateProjectConfigurationJob;

public class ForgeWizardHelper {

	private ImportNewProjectsJob importNewProjectsJob;
	private RefreshInitialSelectionJob refreshInitialSelectionJob;
	private UpdateProjectConfigurationJob updateProjectConfigurationJob;
	private ShowFinalSelectionJob showFinalSelectionJob;
	
	public ForgeWizardHelper() {
		initializeJobs();
	}
	
	private void initializeJobs() {
		importNewProjectsJob = new ImportNewProjectsJob();
		refreshInitialSelectionJob = new RefreshInitialSelectionJob();
		updateProjectConfigurationJob = new UpdateProjectConfigurationJob();
		showFinalSelectionJob = new ShowFinalSelectionJob();
		importNewProjectsJob.setSuccessor(refreshInitialSelectionJob);
		refreshInitialSelectionJob.setSuccessor(updateProjectConfigurationJob);
		updateProjectConfigurationJob.setSuccessor(showFinalSelectionJob);
	}

	public void onFinish(final UIContextImpl context) {
		refreshInitialSelectionJob.setContext(context);
		showFinalSelectionJob.setContext(context);
		importNewProjectsJob.schedule();
	}

	public void onCancel(UIContextImpl context) {
		updateProjectConfigurationJob.setPomFile(null);
	}

	public void onCreate(UIContextImpl context) {
		UISelectionImpl<?> selection = context.getInitialSelection();
		if (selection != null) {
			IResource resource = selection.getResource();
			if (resource != null) {
				updateProjectConfigurationJob.setPomFile(determinePomFile(selection.getResource()));
			}
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
