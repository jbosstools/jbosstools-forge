/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.tools.forge.ui.ext.context;

import org.eclipse.core.runtime.IProgressMonitor;
import org.jboss.forge.addon.ui.UIProgressMonitor;

/**
 * Adapter for {@link UIProgressMonitor} to {@link IProgressMonitor}
 * implementations
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class UIProgressMonitorAdapter implements UIProgressMonitor {

	private final IProgressMonitor monitor;

	public UIProgressMonitorAdapter(IProgressMonitor monitor) {
		this.monitor = monitor;
	}

	@Override
	public void beginTask(String name, int totalWork) {
		monitor.beginTask(name, totalWork);
	}

	@Override
	public void done() {
		monitor.done();
	}

	@Override
	public boolean isCancelled() {
		return monitor.isCanceled();
	}

	@Override
	public void setCancelled(boolean cancelled) {
		monitor.setCanceled(cancelled);
	}

	@Override
	public void setTaskName(String taskName) {
		monitor.setTaskName(taskName);
	}

	@Override
	public void subTask(String subTaskName) {
		monitor.subTask(subTaskName);
	}

	@Override
	public void worked(int work) {
		monitor.worked(work);
	}

}
