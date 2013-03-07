package org.jboss.tools.forge.ui.wizard.newproject;

import org.eclipse.core.resources.ResourcesPlugin;
import org.jboss.tools.forge.core.process.ForgeRuntime;
import org.jboss.tools.forge.importer.ProjectImporter;
import org.jboss.tools.forge.ui.util.ForgeHelper;
import org.jboss.tools.forge.ui.wizard.AbstractForgeWizard;

public class NewProjectWizard extends AbstractForgeWizard {

	private NewProjectWizardPage newProjectWizardPage = new NewProjectWizardPage();

	public NewProjectWizard() {
		setWindowTitle("Create New Project");
	}

	@Override
	public void addPages() {
		addPage(newProjectWizardPage);
	}

	public void doExecute() {
		ForgeRuntime runtime = ForgeHelper.getDefaultRuntime();
		runtime.sendCommand("cd " + getProjectLocation());
		runtime.sendCommand("new-project --named " + getProjectName());
	}


	public void doRefresh() {
		new ProjectImporter(
				getProjectLocation(), 
				getProjectName())
		.importProject();
	}
	
	private String getProjectLocation() {
		return ResourcesPlugin
				.getWorkspace()
				.getRoot()
				.getLocation()
				.toOSString();
	}
	
	private String getProjectName() {
		return (String)getWizardDescriptor().get(NewProjectWizardPage.PROJECT_NAME);
	}
	
}
