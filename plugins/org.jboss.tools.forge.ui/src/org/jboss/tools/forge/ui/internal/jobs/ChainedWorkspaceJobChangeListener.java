/**
 * Copyright (c) Red Hat, Inc., contributors and others 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.forge.ui.internal.jobs;

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;

public class ChainedWorkspaceJobChangeListener extends JobChangeAdapter {
	
	private ChainedWorkspaceJob job;
	
	public ChainedWorkspaceJobChangeListener(ChainedWorkspaceJob job) {
		this.job = job;
	}

	@Override
	public void done(IJobChangeEvent event) {
		job.scheduleSuccessor();
	}

}
