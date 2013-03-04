package org.jboss.tools.forge.ui.wizard.scaffold;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.jboss.tools.forge.core.preferences.ForgeRuntimesPreferences;
import org.jboss.tools.forge.core.process.ForgeRuntime;
import org.jboss.tools.forge.ui.util.ForgeHelper;
import org.jboss.tools.forge.ui.wizard.WizardsPlugin;

public class ScaffoldWizard extends Wizard implements IWorkbenchWizard {

	private ScaffoldWizardPage scaffoldWizardPage = new ScaffoldWizardPage();

	public ScaffoldWizard() {
		setWindowTitle("Scaffold JPA Entities");
		// setDefaultPageImageDescriptor(ImageDescriptor.createFromFile(
		// ScaffoldWizard.class, "ScaffoldEntitiesWizBan.png"));
	}

	@Override
	public void addPages() {
		addPage(scaffoldWizardPage);
	}

	@Override
	public boolean performFinish() {
		Job job = new WorkspaceJob("Scaffolding JPA Entities") {
			@Override
			public IStatus runInWorkspace(IProgressMonitor monitor)
					throws CoreException {
				generateEntities(scaffoldWizardPage.getTargetProject());
				return Status.OK_STATUS;
			}
		};
		job.schedule();
		return true;
	}

	private void generateEntities(IProject project) {
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
		runtime.sendCommand("cd " + project.getLocation().toOSString());
		runtime.sendCommand("generate-entities --connection-profile sakila");
		runtime.sendCommand("scaffold setup");
		runtime.sendCommand("scaffold from-entity ~.model.* --overwrite");
		runtime.sendCommand("cd " + currentDir);
		runtime.sendCommand("set ACCEPT_DEFAULTS "
				+ (acceptDefaults ? "true" : "false"));
		refreshProject(project);
	}

	private void refreshProject(IProject project) {
		try {
			project.getWorkspace()
					.getRoot()
					.refreshLocal(IResource.DEPTH_INFINITE,
							new NullProgressMonitor());
			// project.refreshLocal(IResource.DEPTH_INFINITE, null);
			MavenPlugin.getProjectConfigurationManager()
					.updateProjectConfiguration(project,
							new NullProgressMonitor());
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
				String taskName = "Please wait while the scaffold generator is starting";
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

}
