package org.jboss.tools.forge.ui.wizard.scaffold;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.jboss.tools.forge.core.process.ForgeRuntime;
import org.jboss.tools.forge.ui.util.ForgeHelper;
import org.jboss.tools.forge.ui.wizard.AbstractForgeWizard;

public class ScaffoldEntitiesWizard extends AbstractForgeWizard {

	private ScaffoldEntitiesWizardPage scaffoldEntitiesWizardPage = new ScaffoldEntitiesWizardPage();

	public ScaffoldEntitiesWizard() {
		setWindowTitle("Scaffold Entities");
	}

	@Override
	public void addPages() {
		addPage(scaffoldEntitiesWizardPage);
	}

	@Override
	public void doExecute() {
		ForgeRuntime runtime = ForgeHelper.getDefaultRuntime();
		runtime.sendCommand("cd " + getProjectLocation());
		for (String entityName : getEntityNames()) {
			runtime.sendCommand("scaffold from-entity " + entityName + ".java");
		}
	}
	
	@Override
	public void doRefresh() {
		IProject project = getProject(getProjectName());
		refreshResource(project);
		updateProjectConfiguration(project);
	}
	
	@Override
	public String getStatusMessage() {
		return "Scaffolding entities for project '" + getProjectName() + "'.";
	}
	
	private String getProjectName() {
		return (String)getWizardDescriptor().get(ScaffoldEntitiesWizardPage.PROJECT_NAME);
	}
	
	@SuppressWarnings("unchecked")
	private List<String> getEntityNames() {
		return (List<String>)getWizardDescriptor().get(ScaffoldEntitiesWizardPage.ENTITY_NAMES);
	}
	
	private String getProjectLocation() {
		return getProject(getProjectName()).getLocation().toOSString();
	}
	
}
