package org.jboss.tools.forge.ui.wizard.project;

import org.eclipse.core.resources.ResourcesPlugin;
import org.jboss.tools.forge.importer.ProjectImporter;
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

	@Override
	public void doExecute() {
//		ForgeRuntime runtime = ForgeHelper.getDefaultRuntime();
//		runtime.sendCommand("cd " + getProjectLocation());
//		runtime.sendCommand("new-project --named " + getProjectName());
		executeNewProject();
		if (needsPersistenceSetup()) {
			executePersistenceSetup();
		}
	}
	
	private void executePersistenceSetup() {
		String command = "persistence setup ";
		command += " --provider " + getProviderName();
		command += " --container " + getContainerName();
		System.out.println(command);
	}
	
	private String getProviderName() {
		return (String)getWizardDescriptor().get(NewProjectWizardPage.PROVIDER_NAME);
	}
	
	private String getContainerName() {
		return (String)getWizardDescriptor().get(NewProjectWizardPage.CONTAINER_NAME);
	}
	
	private void executeNewProject() {
		String command = "new-project";
		command += " --named " + getProjectName();
		command += " --projectFolder " + getProjectLocation();
		String topLevelPackage = getTopLevelPackage();
		if (topLevelPackage != null) {
			command += " --topLevelPackage " + topLevelPackage;
		}
//		String type = getProjectType();
//		if (type != null) {
//			command += " --type " + type;
//		}
		if (createMain()) {
			command += " --createMain ";
		}
		String finalName = getFinalName();
		if (finalName != null) {
			command += " --finalName " + finalName;
		}
		System.out.println(command);
	}
	
	private String getFinalName() {
		return notEmptyString(
				(String)getWizardDescriptor().get(NewProjectWizardPage.FINAL_NAME));
	}
	
	private boolean createMain() {
		Object obj = getWizardDescriptor().get(NewProjectWizardPage.CREATE_MAIN);
		if (obj != null && obj instanceof Boolean) {
			return ((Boolean)obj).booleanValue();
		} else {
			return false;
		}
	}
	
//	private String getProjectType() {
//		Object obj = getWizardDescriptor().get(NewProjectWizardPage.PROJECT_TYPE);
//		return notEmptyString(
//				(String)obj);
//	}
	
	private String getTopLevelPackage() {
		return notEmptyString(
				(String)getWizardDescriptor().get(NewProjectWizardPage.TOP_LEVEL_PACKAGE));
	}
	
	private String notEmptyString(String str) {
		if (str != null && !"".equals(str)) {
			return str;
		} else {
			return null;
		}
	}
	
	private boolean needsPersistenceSetup() {
		Object obj = getWizardDescriptor().get(NewProjectWizardPage.SETUP_PERSISTENCE);
		if (obj != null && obj instanceof Boolean) {
			return ((Boolean)obj).booleanValue();			
		} else {
			return false;
		}
	}

	@Override
	public void doRefresh() {
		new ProjectImporter(
				getProjectLocation(), 
				getProjectName())
		.importProject();
	}
	
	@Override
	public String getStatusMessage() {
		return "Creating new project '" + getProjectName() + "'.";
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
