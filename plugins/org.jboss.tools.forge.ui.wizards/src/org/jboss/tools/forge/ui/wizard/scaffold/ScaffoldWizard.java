package org.jboss.tools.forge.ui.wizard.scaffold;

import java.util.Collections;
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

public class ScaffoldWizard extends AbstractForgeWizard {

	private ScaffoldProjectWizardPage scaffoldProjectWizardPage = new ScaffoldProjectWizardPage();

	boolean setupNeeded = false;
	private boolean busy = false;

	public ScaffoldWizard() {
		setWindowTitle("Scaffold Entities");
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection sel) {
		super.init(workbench, sel);
		doInit(workbench, sel);
	}
	
	private void doInit(IWorkbench workbench, final IStructuredSelection sel) {
		initializeProject(sel);
		Runnable runner = new Runnable() {
			@Override
			public void run() {
				if (!isAngularJsPluginAvailable()) {
					new AngularJsInstaller().install(getShell());
				}
			}			
		};
		new Thread(runner).start();
	}
	
	private boolean isAngularJsPluginAvailable() {
		String str = ForgeHelper.getDefaultRuntime().sendCommand("forge list-plugins");
		return str != null && str.contains("org.jboss.forge.angularjs-scaffoldx-plugin");
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
							ScaffoldProjectWizardPage.PROJECT_NAME, 
							project.getName());
					return;
				}
			}
		}
	}

	@Override
	public void addPages() {
		addPage(scaffoldProjectWizardPage);
	}

	@Override
	public void doExecute() {
		ForgeRuntime runtime = ForgeHelper.getDefaultRuntime();
		runtime.sendCommand("cd " + getProjectLocation());
		if (setupNeeded) {
			String scaffoldType = (String)getWizardDescriptor().get(ScaffoldProjectWizardPage.SCAFFOLD_TYPE);
			runtime.sendCommand("scaffold-x setup --scaffoldType " + scaffoldType);
		}
		for (String entityName : getEntityNames()) {
			runtime.sendCommand("scaffold-x from " + entityName);
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
		return (String)getWizardDescriptor().get(ScaffoldProjectWizardPage.PROJECT_NAME);
	}
	
	@SuppressWarnings("unchecked")
	private List<String> getEntityNames() {
		List<String> result = (List<String>)getWizardDescriptor().get(ScaffoldProjectWizardPage.ENTITY_NAMES);
		if (result == null) {
			result = Collections.EMPTY_LIST;
		}
		return result;
	}
	
	String getProjectLocation() {
		return getProject(getProjectName()).getLocation().toOSString();
	}
	
	void checkIfSetupNeeded() {
		setBusy(true);
		new ScaffoldWizardHelper(this).checkIfSetupNeeded();
	}
	
	void setBusy(boolean b) {
		busy = b;
		scaffoldProjectWizardPage.updatePageComplete();
	}
	
	void setSetupNeeded(boolean b) {
		setupNeeded = b;
	}
	
	boolean isBusy() {
		return busy;
	}
	
}
