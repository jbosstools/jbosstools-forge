package org.jboss.tools.forge.ui.wizard.entity;

import org.eclipse.core.resources.IProject;
import org.jboss.tools.forge.core.process.ForgeRuntime;
import org.jboss.tools.forge.ui.util.ForgeHelper;
import org.jboss.tools.forge.ui.wizard.AbstractForgeWizard;

public class NewEntityWizard extends AbstractForgeWizard {

	private NewEntityWizardPage newEntityWizardPage = new NewEntityWizardPage();

	public NewEntityWizard() {
		setWindowTitle("Create New Entity");
	}

	@Override
	public void addPages() {
		addPage(newEntityWizardPage);
	}

	@Override
	public void doExecute() {
		ForgeRuntime runtime = ForgeHelper.getDefaultRuntime();
		runtime.sendCommand("cd " + getProjectLocation());
		runtime.sendCommand("entity --named " + getEntityName());
	}
	
	@Override
	public void doRefresh() {
		refreshResource(getProject(getProjectName()));
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
