package org.jboss.tools.forge.ui.wizard;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.jboss.tools.forge.core.process.ForgeRuntime;
import org.jboss.tools.forge.importer.ProjectConfigurationUpdater;
import org.jboss.tools.forge.ui.util.ForgeHelper;
import org.jboss.tools.forge.ui.wizards.WizardsPlugin;

public abstract class AbstractForgeWizard extends Wizard implements IForgeWizard {

	private HashMap<Object, Object> wizardDescriptor = null;
	
	private String startDir = null;
	private boolean acceptDefaults = false;
	
	@Override
	public void init(IWorkbench workbench, IStructuredSelection sel) {
		try {
			if (!ForgeHelper.isForgeRunning() && !ForgeHelper.isForgeStarting()) {
				startForge();
			}
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
	
	@Override
	public boolean performFinish() {
		try {
			getContainer().run(true, true, new IRunnableWithProgress() {				
				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException,
						InterruptedException {
					monitor.beginTask(getStatusMessage(), IProgressMonitor.UNKNOWN);
					execute();
				}
			});
		} catch (Exception e) {
			// ignore
		}
		return true;
	}
	
	protected void execute() {
		doBefore();
		doExecute();
		doAfter();
		doRefresh();
	}
	
	@Override
	public boolean needsProgressMonitor() {
		return true;
	}
	
	protected void doBefore() {
		ForgeRuntime runtime = ForgeHelper.getDefaultRuntime();
		startDir = runtime.sendCommand("pwd").trim();
		acceptDefaults = false;
		String variables = runtime.sendCommand("set");
		int start = variables.indexOf("ACCEPT_DEFAULTS=");
		if (start != -1) {
			start = start + "ACCEPT_DEFAULTS=".length();
			int end = variables.indexOf('\n', start);
			acceptDefaults = end > start
					&& "true".equals(variables.substring(start, end));
		}
		runtime.sendCommand("set ACCEPT_DEFAULTS true");
	}
	
	protected void doAfter() {
		ForgeRuntime runtime = ForgeHelper.getDefaultRuntime();
		runtime.sendCommand("cd " + startDir);
		runtime.sendCommand("set ACCEPT_DEFAULTS "
				+ (acceptDefaults ? "true" : "false"));
	}
	
	protected IProject getProject(String projectName) {
		return ResourcesPlugin
				.getWorkspace()
				.getRoot()
				.getProject(projectName);
	}

	protected void refreshResource(IResource project) {
		try {
			project.refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (CoreException e) {
			WizardsPlugin.log(e);
		}
	}
	
	protected void updateProjectConfiguration(IProject project) {
		ProjectConfigurationUpdater.updateProject(project);
	}
	
	public Map<Object, Object> getWizardDescriptor() {
		if (wizardDescriptor == null) {
			wizardDescriptor = new HashMap<Object, Object>();
		}
		return wizardDescriptor;
	}
	
//	protected void displayNotification(final String title, final String message) {
//		Display.getDefault().asyncExec(new Runnable() {
//			@Override
//			public void run() {
//				NotifierDialog.notify(title, message, NotificationType.INFO);
//			}			
//		});
//	}

}
