package org.jboss.tools.forge.ui.wizard.reveng;

import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;
import org.jboss.tools.forge.core.process.ForgeRuntime;
import org.jboss.tools.forge.ui.util.ForgeHelper;
import org.jboss.tools.forge.ui.wizard.AbstractForgeWizard;
import org.jboss.tools.forge.ui.wizard.util.WizardsHelper;

public class GenerateEntitiesWizard extends AbstractForgeWizard {

	private ConnectionProfileWizardPage generateEntitiesWizardPage = new ConnectionProfileWizardPage();

	public GenerateEntitiesWizard() {
		setWindowTitle("Generate Entities");
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
							ConnectionProfileWizardPage.PROJECT_NAME, 
							project.getName());
					return;
				}
			}
		}
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
		return (String)getWizardDescriptor().get(ConnectionProfileWizardPage.PROJECT_NAME);
	}
	
	private String getConnectionProfile() {
		return (String)getWizardDescriptor().get(ConnectionProfileWizardPage.CONNECTION_PROFILE);
	}
	
	private String getProjectLocation() {
		return getProject(getProjectName()).getLocation().toOSString();
	}
	
}
