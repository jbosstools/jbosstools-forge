package org.jboss.tools.forge.ui.wizards.internal.wizard.reveng;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.jboss.tools.forge.core.runtime.ForgeRuntime;
import org.jboss.tools.forge.ui.util.ForgeHelper;
import org.jboss.tools.forge.ui.wizards.internal.WizardsPlugin;

public class HibernateToolsInstaller {
	
	private boolean done = false;
//	private String prompt = null;
//	private String promptNoProject = null;
	
	void install(Shell shell) {
		final ProgressMonitorDialog pmd = new ProgressMonitorDialog(shell);
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				try {
					pmd.run(true, true, new IRunnableWithProgress() {
						@Override
						public void run(IProgressMonitor monitor)
								throws InvocationTargetException, InterruptedException {
							String taskName = "Please wait while Hibernate Tools is being installed.";
							monitor.beginTask(taskName, IProgressMonitor.UNKNOWN);
							Runnable installer = new Runnable() {
								@Override
								public void run() {
									ForgeRuntime runtime = ForgeHelper.getDefaultRuntime();
//									prompt = runtime.sendCommand("get-prompt");
//									promptNoProject = runtime.sendCommand("get-prompt-no-project");		
									runtime.sendCommand("forge install-plugin hibernate-tools");
									runtime.sendInput(System.getProperty("line.separator"));
//									runtime.sendCommand("set-prompt " + prompt);
//									runtime.sendCommand("set-prompt-no-project " + promptNoProject);
									done = true;
								}								
							};
							new Thread(installer).start();
							while (!done) {
								Thread.sleep(1000);
							}
						}
					});
				} catch (Exception e) {
					WizardsPlugin.log(e);
				}
			}
		});	
	}

}
