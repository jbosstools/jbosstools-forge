package org.jboss.tools.forge.ui.wizard.project;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.swt.widgets.Display;
import org.jboss.tools.forge.importer.ProjectImporter;
import org.jboss.tools.forge.ui.util.ForgeHelper;
import org.jboss.tools.forge.ui.wizard.AbstractForgeWizard;

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
	public void doRefresh() {
		IJobChangeListener jobChangeListener = new JobChangeAdapter() {
			@Override
			public void done(IJobChangeEvent event) {
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						listener.propertyChange(new PropertyChangeEvent(this, null, null, getProjectName()));
					}					
				});
			}
		};
		ProjectImporter importer = 
				new ProjectImporter(
						getProjectLocation(), 
						getProjectName(),
						jobChangeListener);
		importer.importProject();
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
}
