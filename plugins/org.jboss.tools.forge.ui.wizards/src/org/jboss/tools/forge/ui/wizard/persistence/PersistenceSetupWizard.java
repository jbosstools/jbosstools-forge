package org.jboss.tools.forge.ui.wizard.persistence;

import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;
import org.jboss.tools.forge.core.process.ForgeRuntime;
import org.jboss.tools.forge.ui.util.ForgeHelper;
import org.jboss.tools.forge.ui.wizard.AbstractForgeWizard;
import org.jboss.tools.forge.ui.wizard.util.WizardsHelper;

public class PersistenceSetupWizard extends AbstractForgeWizard {

	private PersistenceSetupWizardPage persistenceSetupWizardPage = new PersistenceSetupWizardPage();

	public PersistenceSetupWizard() {
		setWindowTitle("Set Up Persistence");
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
			if (object instanceof IProject) {
				IProject project = (IProject)object;
				if (!WizardsHelper.isJPAProject(project)) {
					getWizardDescriptor().put(
							PersistenceSetupWizardPage.PROJECT_NAME, 
							project.getName());
					return;
				}
			}
		}
	}

	@Override
	public void addPages() {
		addPage(persistenceSetupWizardPage);
	}

	@Override
	public void doExecute() {
		ForgeRuntime runtime = ForgeHelper.getDefaultRuntime();
		runtime.sendCommand("cd " + getProjectLocation());
		runtime.sendCommand(
				"persistence setup" +
				" --provider " + getProviderName() + 
				" --container " + getContainerName());
	}
	
	@Override
	public void doRefresh() {
		IProject project = getProject(getProjectName());
		refreshResource(project);
		updateProjectConfiguration(project);
	}
	
	@Override
	public String getStatusMessage() {
		return "Setting up persistence for project '" + getProjectName() + "'.";
	}
	
	private String getProjectName() {
		return (String)getWizardDescriptor().get(PersistenceSetupWizardPage.PROJECT_NAME);
	}
	
	private String getProviderName() {
		return (String)getWizardDescriptor().get(PersistenceSetupWizardPage.PROVIDER_NAME);
	}
	
	private String getContainerName() {
		return (String)getWizardDescriptor().get(PersistenceSetupWizardPage.CONTAINER_NAME);
	}
	
	private String getProjectLocation() {
		return getProject(getProjectName()).getLocation().toOSString();
	}
	
}
