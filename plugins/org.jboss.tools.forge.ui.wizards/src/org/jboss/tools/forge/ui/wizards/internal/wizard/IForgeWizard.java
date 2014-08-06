/**
 * Copyright (c) Red Hat, Inc., contributors and others 2004 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.forge.ui.wizards.internal.wizard;

import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IWorkbenchWizard;
import org.jboss.tools.forge.core.runtime.ForgeRuntime;

public interface IForgeWizard extends IWorkbenchWizard {

	void doExecute(IProgressMonitor monitor);
	void doRefresh(IProgressMonitor monitor);
	Map<Object, Object> getWizardDescriptor();
	String getStatusMessage();
	ForgeRuntime getRuntime();
}
