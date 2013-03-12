package org.jboss.tools.forge.ui.wizard.entity;

import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;
import org.jboss.tools.forge.core.process.ForgeRuntime;
import org.jboss.tools.forge.ui.util.ForgeHelper;
import org.jboss.tools.forge.ui.wizard.AbstractForgeWizard;
import org.jboss.tools.forge.ui.wizard.util.WizardsHelper;

public class NewEntityWizard extends AbstractForgeWizard {

	private NewEntityWizardPage newEntityWizardPage = new NewEntityWizardPage();

	public NewEntityWizard() {
		setWindowTitle("Create New Entity");
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
				if (WizardsHelper.isJPAProject(project)) {
					getWizardDescriptor().put(
							NewEntityWizardPage.PROJECT_NAME, 
							project.getName());
					return;
				}
			}
		}
	}

	@Override
	public void addPages() {
		addPage(newEntityWizardPage);
	}

	@Override
	public void doExecute() {
		ForgeRuntime runtime = ForgeHelper.getDefaultRuntime();
		runtime.sendCommand("cd " + getProjectLocation());
		runtime.sendCommand("entity --named " + getEntityName());
	}
	
	@Override
	public void doRefresh() {
		refreshResource(getProject(getProjectName()));
	}
	
	@Override
	public String getStatusMessage() {
		return "Creating new entity '" + getEntityName() + "'.";
	}
	
	private String getProjectName() {
		return (String)getWizardDescriptor().get(NewEntityWizardPage.PROJECT_NAME);		
	}
	
	private String getEntityName() {
		return (String)getWizardDescriptor().get(NewEntityWizardPage.ENTITY_NAME);		
	}
	
	private String getProjectLocation() {
		Object projectName = getWizardDescriptor().get(NewEntityWizardPage.PROJECT_NAME);
		IProject project = getProject((String)projectName);
		return project.getLocation().toOSString();
	}
	
}
