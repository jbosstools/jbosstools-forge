package org.jboss.tools.forge.ui.wizard.project;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.maven.model.Model;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.project.MavenProjectInfo;
import org.eclipse.m2e.core.project.ProjectImportConfiguration;
import org.eclipse.swt.widgets.Display;
import org.jboss.tools.forge.ui.util.ForgeHelper;
import org.jboss.tools.forge.ui.wizard.AbstractForgeWizard;
import org.jboss.tools.forge.ui.wizards.WizardsPlugin;

public class NewProjectWizard extends AbstractForgeWizard {

	private NewProjectWizardPage newProjectWizardPage = new NewProjectWizardPage();
	private PropertyChangeListener listener;
	
	public NewProjectWizard(PropertyChangeListener listener) {
		this();
		this.listener = listener;
	}

	public NewProjectWizard() {
		setWindowTitle("Create New Project");
	}

	@Override
	public void addPages() {
		addPage(newProjectWizardPage);
	}

	@Override
	public void doExecute() {
		executeNewProject();
		if (needsPersistenceSetup()) {
			executePersistenceSetup();
		}
	}
	
	private void executePersistenceSetup() {
		String command = "persistence setup ";
		command += " --provider " + getProviderName();
		command += " --container " + getContainerName();
		ForgeHelper.getDefaultRuntime().sendCommand(command);
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
		command += " --projectFolder " + getProjectFolder();
		String topLevelPackage = getTopLevelPackage();
		if (topLevelPackage != null) {
			command += " --topLevelPackage " + topLevelPackage;
		}
		String type = getProjectType();
		if (type != null) {
			command += " --type " + type;
		}
		if (createMain()) {
			command += " --createMain ";
		}
		String finalName = getFinalName();
		if (finalName != null) {
			command += " --finalName " + finalName;
		}
		ForgeHelper.getDefaultRuntime().sendCommand(command);
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
	
	private String getProjectType() {
		Object obj = getWizardDescriptor().get(NewProjectWizardPage.PROJECT_TYPE);
		return notEmptyString(
				((ProjectType)obj).getName());
	}
	
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
	public String getStatusMessage() {
		return "Creating new project '" + getProjectName() + "'.";
	}
	
	private String getProjectLocation() {
		return (String)getWizardDescriptor().get(NewProjectWizardPage.PROJECT_LOCATION);
	}
	
	private String getProjectName() {
		return (String)getWizardDescriptor().get(NewProjectWizardPage.PROJECT_NAME);
	}
	
	private String getProjectFolder() {
		return getProjectLocation() + File.separator + getProjectName();
	}
	
	@Override
	public void doRefresh() {
		try {
			MavenPlugin.getProjectConfigurationManager().importProjects(
					getProjectToImport(getProjectLocation(), getProjectName()), 
				  new ProjectImportConfiguration(), 
				  new NullProgressMonitor());
			IProject project = 
					ResourcesPlugin.getWorkspace().getRoot().getProject(getProjectName());
      	  	MavenPlugin.getProjectConfigurationManager().updateProjectConfiguration(
    			  project, 
    			  new NullProgressMonitor());
      	  Display.getDefault().syncExec(new Runnable() {
				@Override
				public void run() {
					listener.propertyChange(new PropertyChangeEvent(this, null, null, getProjectName()));
				}
				
			});
		} catch (CoreException e) {
			WizardsPlugin.log(e);
		}
	}
	
	private MavenProjectInfo createMavenProjectInfo(String location, String name) {
		MavenProjectInfo result = null;
		try {
			File projectDir = new File(location, name);
			File pomFile = new File(projectDir, "pom.xml");
			Model model = MavenPlugin.getMavenModelManager().readMavenModel(pomFile);
			String pomName = name + "/" + "pom.xml";
			result = new MavenProjectInfo(pomName, pomFile, model, null);
		} catch (CoreException e) {
			
		}
		return result;
	}
	
	private Collection<MavenProjectInfo> getProjectToImport(String location, String name) {
		ArrayList<MavenProjectInfo> result = new ArrayList<MavenProjectInfo>(1);
		result.add(createMavenProjectInfo(location, name));
		return result;
	}

}
