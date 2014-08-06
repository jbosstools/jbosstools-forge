/**
 * Copyright (c) Red Hat, Inc., contributors and others 2013 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.forge.ui.wizards.internal.wizard.entity;

import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;
import org.jboss.tools.forge.ui.wizards.internal.wizard.AbstractForgeWizard;
import org.jboss.tools.forge.ui.wizards.internal.wizard.util.WizardsHelper;

public class NewEntityWizard extends AbstractForgeWizard {

	private NewEntityWizardPage newEntityWizardPage = new NewEntityWizardPage();

	public NewEntityWizard() {
		setWindowTitle("Create New Entity");
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
				if (WizardsHelper.isJPAProject(project)) {
					getWizardDescriptor().put(
							NewEntityWizardPage.PROJECT_NAME, 
							project.getName());
					return;
				}
			}
		}
	}

	@Override
	public void addPages() {
		addPage(newEntityWizardPage);
	}

	@Override
	public void doExecute(IProgressMonitor monitor) {
		sendRuntimeCommand("cd " + getProjectLocation(), monitor);
		sendRuntimeCommand("entity --named " + getEntityName(), monitor);
	}
	
	@Override
	protected int getAmountOfWorkExecute() {
		return 2;
	}
	
	@Override
	public void doRefresh(IProgressMonitor monitor) {
		refreshResource(getProject(getProjectName()), monitor);
	}
	
	@Override
	public String getStatusMessage() {
		return "Creating new entity '" + getEntityName() + "'.";
	}
	
	private String getProjectName() {
		return (String)getWizardDescriptor().get(NewEntityWizardPage.PROJECT_NAME);		
	}
	
	private String getEntityName() {
		return (String)getWizardDescriptor().get(NewEntityWizardPage.ENTITY_NAME);		
	}
	
	private String getProjectLocation() {
		Object projectName = getWizardDescriptor().get(NewEntityWizardPage.PROJECT_NAME);
		IProject project = getProject((String)projectName);
		return project.getLocation().toOSString();
	}
	
}
