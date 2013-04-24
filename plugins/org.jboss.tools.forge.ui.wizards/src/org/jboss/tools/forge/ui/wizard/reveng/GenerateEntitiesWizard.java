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
		doInit(workbench, sel);
	}
	
	private void doInit(IWorkbench workbench, final IStructuredSelection sel) {
		Runnable runner = new Runnable() {
			@Override
			public void run() {
				if (!isHibernateToolsPluginAvailable()) {
					new HibernateToolsInstaller().install(getShell());
				}
				initializeProject(sel);
			}			
		};
		new Thread(runner).start();
	}
	
	private boolean isHibernateToolsPluginAvailable() {
		String str = ForgeHelper.getDefaultRuntime().sendCommand("forge list-plugins");
		return str != null && str.contains("org.jboss.hibernate.forge.hibernate-tools-plugin");
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
		String generateCommand = 
				"generate-entities" +
				" --url " + getConnectionProfile().url +
				" --user " + getConnectionProfile().user +
				" --dialect " + getConnectionProfile().dialect +
				" --driver " + getConnectionProfile().driverClass +
				" --pathToDriver " + getConnectionProfile().driverLocation;
		if (getConnectionProfile().password != null 
				&& !"".equals(getConnectionProfile().password)) {
			generateCommand += " --password " + getConnectionProfile().password;
		}
		runtime.sendCommand(generateCommand);
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
	
	private ConnectionProfileDescriptor getConnectionProfile() {
		return (ConnectionProfileDescriptor)getWizardDescriptor().get(ConnectionProfileWizardPage.CONNECTION_PROFILE);
	}
	
	private String getProjectLocation() {
		return getProject(getProjectName()).getLocation().toOSString();
	}
	
	String prompt = null;
	String promptNoProject = null;
	
	@Override
	protected void doBefore() {
		super.doBefore();
		ForgeRuntime runtime = ForgeHelper.getDefaultRuntime();
		prompt = runtime.sendCommand("get-prompt");
		promptNoProject = runtime.sendCommand("get-prompt-no-project");		
	}
	
	@Override
	protected void doAfter() {
		ForgeRuntime runtime = ForgeHelper.getDefaultRuntime();
		runtime.sendCommand("set-prompt " + prompt);
		runtime.sendCommand("set-prompt-no-project " + promptNoProject);
		super.doAfter();
	}
	
}
