package org.jboss.tools.forge.ui.wizard.scaffold;

import org.eclipse.core.resources.IProject;
import org.jboss.tools.forge.core.process.ForgeRuntime;
import org.jboss.tools.forge.ui.util.ForgeHelper;
import org.jboss.tools.forge.ui.wizard.AbstractForgeWizard;

public class ScaffoldSetupWizard extends AbstractForgeWizard {

	private ScaffoldSetupWizardPage scaffoldSetupWizardPage = new ScaffoldSetupWizardPage();

	public ScaffoldSetupWizard() {
		setWindowTitle("Scaffold Setup");
	}

	@Override
	public void addPages() {
		addPage(scaffoldSetupWizardPage);
	}

	@Override
	public void doExecute() {
		ForgeRuntime runtime = ForgeHelper.getDefaultRuntime();
		runtime.sendCommand("cd " + getProjectLocation());
		runtime.sendCommand("scaffold setup");
	}
	
	@Override
	public void doRefresh() {
		IProject project = getProject(getProjectName());
		refreshResource(project);
		updateProjectConfiguration(project);
	}
	
	@Override
	public String getStatusMessage() {
		return "Set up scaffolding for project '" + getProjectName() + "'.";
	}
	
	private String getProjectName() {
		return (String)getWizardDescriptor().get(ScaffoldSetupWizardPage.PROJECT_NAME);
	}
	
	private String getProjectLocation() {
		return getProject(getProjectName()).getLocation().toOSString();
	}
	
}
