/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.tools.forge.ui.ext;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Shell;
import org.jboss.forge.addon.ui.UIProgressMonitor;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.input.UIPrompt;
import org.jboss.forge.addon.ui.spi.UIRuntime;
import org.jboss.tools.forge.ui.ext.context.UIProgressMonitorAdapter;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ForgeUIRuntime implements UIRuntime {

	@Override
	public UIProgressMonitor createProgressMonitor(UIContext context) {
		IProgressMonitor progressMonitor = (IProgressMonitor) context
				.getAttributeMap().get(IProgressMonitor.class);
		UIProgressMonitorAdapter monitor = new UIProgressMonitorAdapter(
				progressMonitor);
		return monitor;
	}

	@Override
	public UIPrompt createPrompt(UIContext context) {
		Shell shell = (Shell) context.getAttributeMap().get(Shell.class);
		return new ForgeUIPrompt(shell);
	}

}
