/**
 * Copyright (c) Red Hat, Inc., contributors and others 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.forge.ui.internal.jobs;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.jboss.tools.forge.ui.internal.ext.importer.ImportEclipseProjectListener;

public class ImportNewProjectsJob extends ChainedWorkspaceJob {

	public ImportNewProjectsJob() {
		super("Import New Projects");
	}

	@Override
	public IStatus runInWorkspace(IProgressMonitor monitor)
			throws CoreException {
		if (ImportEclipseProjectListener.INSTANCE.projectsAvailableForImport()) {
			ImportEclipseProjectListener.INSTANCE.doImport();
		}
		return Status.OK_STATUS;
	}

}
