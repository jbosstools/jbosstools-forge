/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.tools.forge.ui.ext;

import org.eclipse.core.runtime.IProgressMonitor;
import org.jboss.forge.addon.ui.UIProgressMonitor;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.spi.UIRuntime;
import org.jboss.tools.forge.ui.ext.context.UIProgressMonitorAdapter;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ForgeUIRuntime implements UIRuntime {

	private IProgressMonitor progressMonitor;

	@Override
	public UIProgressMonitor createProgressMonitor(UIContext context) {
		UIProgressMonitorAdapter monitor = new UIProgressMonitorAdapter(
				progressMonitor);
		progressMonitor = null;
		return monitor;
	}

	/**
	 * @param progressMonitor
	 *            the progressMonitor to set
	 */
	public void setProgressMonitor(IProgressMonitor progressMonitor) {
		this.progressMonitor = progressMonitor;
	}

}
