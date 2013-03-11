package org.jboss.tools.forge.ui.wizard.reveng;

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
		System.out.println(ForgeHelper.getDefaultRuntime().sendCommand("connection-profiles list"));;
//		ForgeRuntime runtime = ForgeHelper.getDefaultRuntime();
//		runtime.sendCommand("cd " + getProjectLocation());
//		for (String entityName : getEntityNames()) {
//			runtime.sendCommand("scaffold from-entity " + entityName + ".java");
//		}
	}
	
	@Override
	public void doRefresh() {
//		IProject project = getProject(getProjectName());
//		refreshResource(project);
//		updateProjectConfiguration(project);
	}
	
	@Override
	public String getStatusMessage() {
		return "Generating entities for project '" + getProjectName() + "'.";
	}
	
	private String getProjectName() {
		return (String)getWizardDescriptor().get(GenerateEntitiesWizardPage.PROJECT_NAME);
	}
	
	private String getProjectLocation() {
		return getProject(getProjectName()).getLocation().toOSString();
	}
	
}
