/**
 * Copyright (c) Red Hat, Inc., contributors and others 2013 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.forge.ui.wizards.internal.wizard;

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardPage;
import org.jboss.tools.forge.ui.wizards.internal.WizardsPlugin;

public abstract class AbstractForgeWizardPage extends WizardPage {
	
	public final String DEFAULT_MESSAGE = "Powered by JBoss Forge.";

	protected AbstractForgeWizardPage(String pageName, String title,
			ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
		setMessage(DEFAULT_MESSAGE);
		setImageDescriptor(WizardsPlugin.imageDescriptorFromPlugin(WizardsPlugin.PLUGIN_ID, "icons/forge.png"));
	}

	@Override
	public IForgeWizard getWizard() {
		IWizard result = super.getWizard();
		if (!(result instanceof IForgeWizard)) {
			RuntimeException e = new RuntimeException("Forge wizard pages need to be hosted by a Forge wizard");
			WizardsPlugin.log(e);
			throw e;
		}
		return (IForgeWizard)result;
	}
	
	protected Map<Object, Object> getWizardDescriptor() {
		return getWizard().getWizardDescriptor();
	}
	
	protected IProject getProject(String projectName) {
		return ResourcesPlugin
				.getWorkspace()
				.getRoot()
				.getProject(projectName);
	}

}
