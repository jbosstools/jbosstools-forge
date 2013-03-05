package org.jboss.tools.forge.ui.wizard.persistence;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.jboss.tools.forge.core.preferences.ForgeRuntimesPreferences;
import org.jboss.tools.forge.core.process.ForgeRuntime;
import org.jboss.tools.forge.importer.ProjectConfigurationUpdater;
import org.jboss.tools.forge.ui.util.ForgeHelper;
import org.jboss.tools.forge.ui.wizard.WizardsPlugin;

public class PersistenceSetupWizard extends Wizard implements IWorkbenchWizard {

	private PersistenceSetupWizardPage persistenceSetupWizardPage = new PersistenceSetupWizardPage();

	public PersistenceSetupWizard() {
		setWindowTitle("Set Up Persistence");
	}

	@Override
	public void addPages() {
		addPage(persistenceSetupWizardPage);
	}

	@Override
	public boolean performFinish() {
		Job job = new WorkspaceJob("Set up persistence") {
			@Override
			public IStatus runInWorkspace(IProgressMonitor monitor)
					throws CoreException {
				setUpPersistence(persistenceSetupWizardPage.getPersistenceDescriptor());
				return Status.OK_STATUS;
			}
		};
		job.schedule();
		return true;
	}

	private void setUpPersistence(PersistenceDescriptor persistenceDescriptor) {
		ForgeRuntime runtime = ForgeRuntimesPreferences.INSTANCE
				.getDefaultRuntime();
		String currentDir = runtime.sendCommand("pwd").trim();
		boolean acceptDefaults = false;
		String variables = runtime.sendCommand("set");
		int start = variables.indexOf("ACCEPT_DEFAULTS=");
		if (start != -1) {
			start = start + "ACCEPT_DEFAULTS=".length();
			int end = variables.indexOf('\n', start);
			acceptDefaults = end > start
					&& "true".equals(variables.substring(start, end));
		}
		runtime.sendCommand("set ACCEPT_DEFAULTS true");
		runtime.sendCommand("cd " + getLocation(persistenceDescriptor.project));
		runtime.sendCommand(
				"persistence setup" +
				" --provider " + persistenceDescriptor.provider + 
				" --container " + persistenceDescriptor.container);
		runtime.sendCommand("cd " + currentDir);
		runtime.sendCommand("set ACCEPT_DEFAULTS "
				+ (acceptDefaults ? "true" : "false"));
		updateProject(persistenceDescriptor.project);
	}
	
	private String getLocation(String projectName) {
		return ResourcesPlugin
				.getWorkspace()
				.getRoot()
				.getProject(projectName)
				.getLocation()
				.toOSString();
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection sel) {
		try {
			startForge();
		} catch (Exception e) {
			WizardsPlugin.log(e);
		}
	}

	private void startForge() throws Exception {
		ProgressMonitorDialog pmd = new ProgressMonitorDialog(getShell());
		pmd.run(true, true, new IRunnableWithProgress() {

			@Override
			public void run(IProgressMonitor monitor)
					throws InvocationTargetException, InterruptedException {
				String taskName = "Please wait while Forge is starting";
				monitor.beginTask(taskName, IProgressMonitor.UNKNOWN);
				Display.getDefault().syncExec(new Runnable() {

					@Override
					public void run() {
						ForgeHelper.startForge();
					}

				});

				while (!ForgeHelper.isForgeRunning()) {
					taskName += ".";
					monitor.setTaskName(taskName);
					Thread.sleep(1000);
				}
			}
		});
	}

	private void updateProject(String projectName) {
		IProject project = ResourcesPlugin
				.getWorkspace()
				.getRoot()
				.getProject(projectName);
		try {
			project.refreshLocal(IResource.DEPTH_INFINITE, null);
			ProjectConfigurationUpdater.updateProject(
					ResourcesPlugin
					.getWorkspace()
					.getRoot()
					.getProject(projectName));
		} catch (CoreException e) {
			WizardsPlugin.log(e);
		}
	}
	
}
