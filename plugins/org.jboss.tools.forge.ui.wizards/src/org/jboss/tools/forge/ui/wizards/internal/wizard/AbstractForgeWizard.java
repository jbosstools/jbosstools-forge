package org.jboss.tools.forge.ui.wizards.internal.wizard;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.jboss.tools.forge.core.preferences.ForgeCorePreferences;
import org.jboss.tools.forge.core.util.ProjectTools;
import org.jboss.tools.forge.ui.util.ForgeHelper;
import org.jboss.tools.forge.ui.wizards.internal.WizardsPlugin;

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
						ForgeHelper.start(ForgeCorePreferences.INSTANCE.getDefaultRuntime());
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
				public void run(final IProgressMonitor monitor) throws InvocationTargetException,
						InterruptedException {
					monitor.beginTask(getStatusMessage(), getTotalAmountOfWork());
					before(monitor);
					execute(monitor);
					after(monitor);
					refresh(monitor);
					monitor.done();
				}
			});
		} catch (Exception e) {
			// ignore
		}
		return true;
	}
	
	protected int getTotalAmountOfWork() {
		return getAmountOfWorkBefore() 
				+ getAmountOfWorkExecute() 
				+ getAmountOfWorkAfter() 
				+ getAmountOfWorkRefresh();
	}
	
	protected void before(IProgressMonitor monitor) {
		SubProgressMonitor subMonitor = new SubProgressMonitor(monitor, getAmountOfWorkBefore());
		subMonitor.beginTask("Preparing...", getAmountOfWorkBefore());
		subMonitor.setTaskName("Preparing...");
		doBefore(subMonitor);
		subMonitor.done();
	}
	
	protected void execute(IProgressMonitor monitor) {
		SubProgressMonitor subMonitor = new SubProgressMonitor(monitor, getAmountOfWorkExecute());
		subMonitor.beginTask("Executing...", getAmountOfWorkExecute());
		subMonitor.setTaskName("Executing...");
		doExecute(subMonitor);
		subMonitor.done();
	}
	
	protected void after(IProgressMonitor monitor) {
		SubProgressMonitor subMonitor = new SubProgressMonitor(monitor, getAmountOfWorkAfter());
		subMonitor.beginTask("Cleaning up...", getAmountOfWorkAfter());
		subMonitor.setTaskName("Cleaning up...");
		doAfter(subMonitor);
		subMonitor.done();
	}
	
	protected void refresh(IProgressMonitor monitor) {
		SubProgressMonitor subMonitor = new SubProgressMonitor(monitor, getAmountOfWorkRefresh());
		subMonitor.beginTask("Refreshing...", getAmountOfWorkRefresh());
		subMonitor.setTaskName("Refreshing...");
		doRefresh(subMonitor);
		subMonitor.done();
	}
	
	@Override
	public boolean needsProgressMonitor() {
		return true;
	}
	
	protected String sendRuntimeCommand(String command, IProgressMonitor monitor) {
		monitor.setTaskName("Executing '" + command + "'");
		String result = ForgeHelper.getDefaultRuntime().sendCommand(command);
		monitor.worked(1);
		return result;
	}
	
	protected void doBefore(IProgressMonitor monitor) {
		startDir = sendRuntimeCommand("pwd", monitor).trim();
		acceptDefaults = false;
		String variables = sendRuntimeCommand("set", monitor);
		int start = variables.indexOf("ACCEPT_DEFAULTS=");
		if (start != -1) {
			start = start + "ACCEPT_DEFAULTS=".length();
			int end = variables.indexOf('\n', start);
			acceptDefaults = end > start
					&& "true".equals(variables.substring(start, end));
		}
		sendRuntimeCommand("set ACCEPT_DEFAULTS true", monitor);
	}
	
	protected int getAmountOfWorkBefore() {
		return 3;
	}
	
	protected void doAfter(IProgressMonitor monitor) {	
		sendRuntimeCommand("cd " + startDir, monitor);
		sendRuntimeCommand("set ACCEPT_DEFAULTS "
				+ (acceptDefaults ? "true" : "false"), monitor);
	}
	
	protected int getAmountOfWorkAfter() {
		return 2;
	}
	
	protected int getAmountOfWorkExecute() {
		return 1;
	}
	
	protected int getAmountOfWorkRefresh() {
		return 1;
	}
	
	protected IProject getProject(String projectName) {
		return ResourcesPlugin
				.getWorkspace()
				.getRoot()
				.getProject(projectName);
	}

	protected void refreshResource(IResource project, IProgressMonitor monitor) {
		try {
			monitor.setTaskName("Refreshing resource " + project.getName());
			project.refreshLocal(IResource.DEPTH_INFINITE, null);
			monitor.worked(1);
		} catch (CoreException e) {
			WizardsPlugin.log(e);
		}
	}
	
	protected void updateProjectConfiguration(IProject project, IProgressMonitor monitor) {
		monitor.setTaskName("Updating configuration of project " + project.getName());
		ProjectTools.updateProjectConfiguration(project);
		monitor.worked(1);
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
