/**
 * Copyright (c) Red Hat, Inc., contributors and others 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.forge.ui.internal.jobs;

import org.eclipse.core.resources.WorkspaceJob;

public abstract class ChainedWorkspaceJob extends WorkspaceJob {
	
	private WorkspaceJob successor;

	public ChainedWorkspaceJob(String name) {
		super(name);
		initializeListener();
	}
	
	private void initializeListener() {
		addJobChangeListener(new ChainedWorkspaceJobChangeListener(this));
	}
	
	public void setSuccessor(WorkspaceJob successor) {
		this.successor = successor;
	}
	
	public void scheduleSuccessor() {
		if (successor != null) {
			successor.schedule();
		}
	}

}
