/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.tools.forge.ui.ext.context;

import org.eclipse.core.runtime.IProgressMonitor;
import org.jboss.forge.addon.ui.UIProgressMonitor;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class UIExecutionContextImpl implements UIExecutionContext {
	private final UIContext uiContext;
	private final UIProgressMonitor monitor;

	public UIExecutionContextImpl(UIContext context, IProgressMonitor monitor) {
		this.uiContext = context;
		this.monitor = new UIProgressMonitorAdapter(monitor);
	}

	@Override
	public UIContext getUIContext() {
		return uiContext;
	}

	@Override
	public UIProgressMonitor getProgressMonitor() {
		return monitor;
	}
}
