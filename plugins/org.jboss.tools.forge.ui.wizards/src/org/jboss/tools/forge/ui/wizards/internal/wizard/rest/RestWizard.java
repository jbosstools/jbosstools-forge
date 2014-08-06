/**
 * Copyright (c) Red Hat, Inc., contributors and others 2013 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.forge.ui.wizards.internal.wizard.rest;

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;
import org.jboss.tools.forge.ui.wizards.internal.wizard.AbstractForgeWizard;
import org.jboss.tools.forge.ui.wizards.internal.wizard.util.WizardsHelper;

public class RestWizard extends AbstractForgeWizard {

	private RestWizardPage restWizardPage = new RestWizardPage();

	public RestWizard() {
		setWindowTitle("Generate REST Endpoints");
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
							RestWizardPage.PROJECT_NAME, 
							project.getName());
					return;
				}
			}
		}
	}

	@Override
	public void addPages() {
		addPage(restWizardPage);
	}

	@Override
	public void doExecute(IProgressMonitor monitor) {
		sendRuntimeCommand("cd " + getProjectLocation(), monitor);
		if (getSetupNeeded()) {
			String activatorType = (String)getWizardDescriptor().get(RestWizardPage.ACTIVATOR_TYPE);
			if (activatorType == null) {
				activatorType = RestWizardPage.ACTIVATOR_TYPE_WEB_XML;
			}
			sendRuntimeCommand("rest setup --activatorType " + activatorType, monitor);
		}
		for (String entityName : getEntityNames()) {
			String command = "rest endpoint-from-entity " + entityName;
			command += " --contentType " + (String)getWizardDescriptor().get(RestWizardPage.CONTENT_TYPE);
			command += " --strategy ROOT_AND_NESTED_DTO";
			sendRuntimeCommand(command, monitor);
		}
	}
	
	private boolean getSetupNeeded() {
		return ((Boolean)getWizardDescriptor().get(RestWizardPage.SETUP_NEEDED)).booleanValue();
	}
	
	@Override
	protected int getAmountOfWorkExecute() {
		int entities = getEntityNames().size();
		return getSetupNeeded() ? entities + 2 : entities + 1;
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
		return "Generating REST endpoints for project '" + getProjectName() + "'.";
	}
	
	private String getProjectName() {
		return (String)getWizardDescriptor().get(RestWizardPage.PROJECT_NAME);
	}
	
	@SuppressWarnings("unchecked")
	private List<String> getEntityNames() {
		return (List<String>)getWizardDescriptor().get(RestWizardPage.ENTITY_NAMES);
	}
	
	String getProjectLocation() {
		return getProject(getProjectName()).getLocation().toOSString();
	}
	
}
