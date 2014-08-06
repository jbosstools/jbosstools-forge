/**
 * Copyright (c) Red Hat, Inc., contributors and others 2013 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.forge.ui.wizards.internal.wizard.persistence;

import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;
import org.jboss.tools.forge.ui.wizards.internal.wizard.AbstractForgeWizard;
import org.jboss.tools.forge.ui.wizards.internal.wizard.util.WizardsHelper;

public class PersistenceSetupWizard extends AbstractForgeWizard {

	private PersistenceSetupWizardPage persistenceSetupWizardPage = new PersistenceSetupWizardPage();

	public PersistenceSetupWizard() {
		setWindowTitle("Set Up Persistence");
	}
	
	@Override
	public void init(IWorkbench workbench, IStructuredSelection sel) {
		super.init(workbench, sel);
		initializeProject(sel);
	}
	
	@SuppressWarnings("rawtypes")
	private void initializeProject(IStructuredSelection sel) {
		Iterator iterator = sel.iterator();
		while (iterator.hasNext()) {
			Object object = iterator.next();
			if (object instanceof IResource) {
				IProject project = ((IResource)object).getProject();
				if (!WizardsHelper.isJPAProject(project)) {
					getWizardDescriptor().put(
							PersistenceSetupWizardPage.PROJECT_NAME, 
							project.getName());
					return;
				}
			}
		}
	}

	@Override
	public void addPages() {
		addPage(persistenceSetupWizardPage);
	}

	@Override
	public void doExecute(IProgressMonitor monitor) {
		sendRuntimeCommand("cd " + getProjectLocation(), monitor);
		sendRuntimeCommand("persistence setup" +
				" --provider " + getProviderName() + 
				" --container " + getContainerName(), monitor);
	}
	
	@Override
	protected int getAmountOfWorkExecute() {
		return 2;
	}
	
	@Override
	public void doRefresh(IProgressMonitor monitor) {
		IProject project = getProject(getProjectName());
		refreshResource(project, monitor);
		updateProjectConfiguration(project, monitor);
	}
	
	@Override
	protected int getAmountOfWorkRefresh() {
		return 2;
	}
	
	@Override
	public String getStatusMessage() {
		return "Setting up persistence for project '" + getProjectName() + "'.";
	}
	
	private String getProjectName() {
		return (String)getWizardDescriptor().get(PersistenceSetupWizardPage.PROJECT_NAME);
	}
	
	private String getProviderName() {
		return (String)getWizardDescriptor().get(PersistenceSetupWizardPage.PROVIDER_NAME);
	}
	
	private String getContainerName() {
		return (String)getWizardDescriptor().get(PersistenceSetupWizardPage.CONTAINER_NAME);
	}
	
	private String getProjectLocation() {
		return getProject(getProjectName()).getLocation().toOSString();
	}
	
}
