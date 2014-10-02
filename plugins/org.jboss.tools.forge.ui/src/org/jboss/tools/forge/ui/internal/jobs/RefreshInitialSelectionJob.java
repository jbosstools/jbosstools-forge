/**
 * Copyright (c) Red Hat, Inc., contributors and others 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.forge.ui.internal.jobs;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.jboss.tools.forge.ui.internal.ForgeUIPlugin;
import org.jboss.tools.forge.ui.internal.ext.context.UIContextImpl;
import org.jboss.tools.forge.ui.internal.ext.context.UISelectionImpl;

public class RefreshInitialSelectionJob extends ChainedWorkspaceJob {

	private UIContextImpl context;

	public RefreshInitialSelectionJob() {
		super("Refresh Initial Selection");
	}

	public void setContext(UIContextImpl context) {
		this.context = context;
	}

	@Override
	public IStatus runInWorkspace(IProgressMonitor monitor)
			throws CoreException {
		try {
			if (context != null) {
				UISelectionImpl<?> selection = context.getInitialSelection();
				if (selection != null) {
					IResource resource = selection.getResource();
					if (resource != null) {
						if (resource.isPhantom()) {
							// resource was deleted
							resource = resource.getParent();
						}
						if (resource != null && resource.getProject() != null) {
							resource.getProject().refreshLocal(
									IResource.DEPTH_INFINITE, null);
						}
					}
				}
			}
		} catch (CoreException e) {
			ForgeUIPlugin.log(e);
		}
		return Status.OK_STATUS;
	}

}
