package org.jboss.tools.forge.ui.internal.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.jboss.tools.forge.core.preferences.ForgeCorePreferences;
import org.jboss.tools.forge.core.runtime.ForgeRuntime;
import org.jboss.tools.forge.core.runtime.ForgeRuntimeState;
import org.jboss.tools.forge.ui.internal.ForgeUIPlugin;
import org.jboss.tools.forge.ui.internal.console.ForgeConsole;
import org.jboss.tools.forge.ui.internal.console.ForgeConsoleManager;
import org.jboss.tools.forge.ui.internal.dialog.ForgeCommandListDialog;
import org.jboss.tools.forge.ui.internal.part.ForgeConsoleView;
import org.jboss.tools.forge.ui.util.ForgeHelper;

public class ForgeCommandListHandler extends AbstractHandler {
	
	private String commandsString;
	private IWorkbenchWindow window;
	private ForgeRuntime runtime;
	
	private Job getCommandsJob = new Job("Get Commands") {	
		@Override
		protected IStatus run(IProgressMonitor progressMonitor) {
			commandsString = runtime.sendCommand("plugin-candidates-query");
			return Status.OK_STATUS;
		}
	};
	
	private IJobChangeListener getCommandsJobListener = new JobChangeAdapter() {
		@Override
		public void done(IJobChangeEvent event) {
			getCommandsJob.removeJobChangeListener(this);
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					new ForgeCommandListDialog(window, runtime, commandsString).open();
				}				
			});
		}
	};

	public Object execute(ExecutionEvent executionEvent) {
		window = HandlerUtil.getActiveWorkbenchWindow(executionEvent);
		if (window == null) {
			return null;
		}				
		showForgeView(window);
		runtime = ForgeCorePreferences.INSTANCE.getDefaultRuntime();
		if (ForgeHelper.isForgeStarting()) {
			showWaitUntilStartedMessage();
		} else if (!ForgeHelper.isForgeRunning()) {
			askUserToStartRuntime(); 
		}
		if (runtime != null && ForgeRuntimeState.RUNNING.equals(runtime.getState())) {
			getCommandsJob.addJobChangeListener(getCommandsJobListener);
			getCommandsJob.schedule();
		}
		return null;
	}
	
	private void showWaitUntilStartedMessage() {
		MessageDialog.openInformation(null, "Forge Starting", "Forge is starting. Please wait until the Forge runtime is started");
	}
	
	private void askUserToStartRuntime() {
		boolean start = MessageDialog.open(
				MessageDialog.QUESTION, 
				null, 
				"Forge Not Running", 
				"Forge is not running. Do you want to start the Forge runtime?", 
				SWT.NONE);
		if (start) {
			Display.getCurrent().asyncExec(new Runnable() {
				@Override
				public void run() {
					ForgeHelper.startForge();
				}				
			});
		}
	}
	
	private void showForgeView(IWorkbenchWindow window) {
		try {
			IViewPart viewPart = window.getActivePage().showView(ForgeConsoleView.ID);
			ForgeRuntime runtime = ForgeCorePreferences.INSTANCE.getDefaultRuntime();
			for (ForgeConsole forgeConsole : ForgeConsoleManager.INSTANCE.getConsoles()) {
				if (runtime == forgeConsole.getRuntime()) {
					((ForgeConsoleView)viewPart).showForgeConsole(forgeConsole);
					break;
				}
			}
			
		} catch (PartInitException e) {
			ForgeUIPlugin.log(e);
		}								
	}

}