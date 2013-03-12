package org.jboss.tools.forge.ui.wizard.reveng;

import org.eclipse.core.resources.IProject;
import org.jboss.tools.forge.core.process.ForgeRuntime;
import org.jboss.tools.forge.ui.util.ForgeHelper;
import org.jboss.tools.forge.ui.wizard.AbstractForgeWizard;

public class GenerateEntitiesWizard extends AbstractForgeWizard {

	private GenerateEntitiesWizardPage generateEntitiesWizardPage = new GenerateEntitiesWizardPage();

	public GenerateEntitiesWizard() {
		setWindowTitle("Generate Entities");
	}

	@Override
	public void addPages() {
		addPage(generateEntitiesWizardPage);
	}
	
	@Override
	public void doExecute() {
		ForgeRuntime runtime = ForgeHelper.getDefaultRuntime();
		runtime.sendCommand("cd " + getProjectLocation());
		runtime.sendCommand("generate-entities --connection-profile " + getConnectionProfile());
	}
	
	@Override
	public void doRefresh() {
		IProject project = getProject(getProjectName());
		refreshResource(project);
		updateProjectConfiguration(project);
	}
	
	@Override
	public String getStatusMessage() {
		return "Generating entities for project '" + getProjectName() + "'.";
	}
	
	private String getProjectName() {
		return (String)getWizardDescriptor().get(GenerateEntitiesWizardPage.PROJECT_NAME);
	}
	
	private String getConnectionProfile() {
		return (String)getWizardDescriptor().get(GenerateEntitiesWizardPage.CONNECTION_PROFILE);
	}
	
	private String getProjectLocation() {
		return getProject(getProjectName()).getLocation().toOSString();
	}
	
}
