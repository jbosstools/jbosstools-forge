/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.tools.forge.ui.ext.listeners;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.jboss.tools.forge.ui.ext.context.UIContextImpl;
import org.jboss.tools.forge.ui.ext.context.UISelectionImpl;
import org.jboss.tools.forge.ui.ext.wizards.WizardListener;

public enum RefreshListener implements WizardListener {
	INSTANCE;
	@Override
	public void onFinish(UIContextImpl context) {
		UISelectionImpl<?> initialSelection = context.getInitialSelection();
		IResource resource = initialSelection.getResource();
		if (resource != null && resource.getProject() != null)
			try {
				// resource.refreshLocal(IResource.DEPTH_ONE, null);
				// Refresh Project
				IProject project = resource.getProject();
				project.refreshLocal(IResource.DEPTH_INFINITE, null);
			} catch (CoreException e) {
				e.printStackTrace();
			}
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

}
