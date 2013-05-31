package org.jboss.tools.forge.ui.wizard.rest;

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;
import org.jboss.tools.forge.core.process.ForgeRuntime;
import org.jboss.tools.forge.ui.util.ForgeHelper;
import org.jboss.tools.forge.ui.wizard.AbstractForgeWizard;
import org.jboss.tools.forge.ui.wizard.util.WizardsHelper;

public class RestWizard extends AbstractForgeWizard {

	private RestWizardPage restWizardPage = new RestWizardPage();

	boolean setupNeeded = false;
	private boolean busy = false;

	public RestWizard() {
		setWindowTitle("Generate REST Endpoints");
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
							RestWizardPage.PROJECT_NAME, 
							project.getName());
					return;
				}
			}
		}
	}

	@Override
	public void addPages() {
		addPage(restWizardPage);
	}

	@Override
	public void doExecute() {
		ForgeRuntime runtime = ForgeHelper.getDefaultRuntime();
		runtime.sendCommand("cd " + getProjectLocation());
		if (setupNeeded) {
			runtime.sendCommand("rest setup --activatorType WEB_XML");
		}
		for (String entityName : getEntityNames()) {
			runtime.sendCommand("rest endpoint-from-entity " + entityName);
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
		return "Generating REST endpoints for project '" + getProjectName() + "'.";
	}
	
	private String getProjectName() {
		return (String)getWizardDescriptor().get(RestWizardPage.PROJECT_NAME);
	}
	
	@SuppressWarnings("unchecked")
	private List<String> getEntityNames() {
		return (List<String>)getWizardDescriptor().get(RestWizardPage.ENTITY_NAMES);
	}
	
	String getProjectLocation() {
		return getProject(getProjectName()).getLocation().toOSString();
	}
	
	void handleProjectChange() {
		setBusy(true);
		checkIfSetupNeeded();
	}
	
	private void checkIfSetupNeeded() {
		new RestSetupHelper(this).checkIfSetupNeeded();
	}
	
	void setBusy(boolean b) {
		busy = b;
		restWizardPage.updatePageComplete();
	}
	
	void setSetupNeeded(boolean b) {
		setupNeeded = b;
	}
	
	boolean isBusy() {
		return busy;
	}
	
}
