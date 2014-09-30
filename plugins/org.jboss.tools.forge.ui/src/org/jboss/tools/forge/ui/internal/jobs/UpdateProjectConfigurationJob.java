/**
 * Copyright (c) Red Hat, Inc., contributors and others 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.forge.ui.internal.jobs;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.jboss.tools.forge.core.util.ProjectTools;

public class UpdateProjectConfigurationJob extends ChainedWorkspaceJob {
	
	private IFile pomFile = null;
	private long pomFileModificationStamp = -1;

	public UpdateProjectConfigurationJob() {
		super("Update Project Configuration");
	}
	
	public void setPomFile(IFile pomFile) {
		this.pomFile = pomFile;
		if (pomFile != null) {
			pomFileModificationStamp = pomFile.getModificationStamp();
		}
	}

	@Override
	public IStatus runInWorkspace(IProgressMonitor monitor)
			throws CoreException {
		if (pomFileModificationStamp != -1 && pomFile != null && pomFile.getModificationStamp() > pomFileModificationStamp) {
			ProjectTools.updateProjectConfiguration(pomFile.getProject());
		}
		pomFile = null;
		pomFileModificationStamp = -1;
		return Status.OK_STATUS;
	}

}
