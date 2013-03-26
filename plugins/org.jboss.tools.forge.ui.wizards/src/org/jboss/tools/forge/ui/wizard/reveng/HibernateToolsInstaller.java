package org.jboss.tools.forge.ui.wizard.reveng;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.jboss.tools.forge.ui.util.ForgeHelper;
import org.jboss.tools.forge.ui.wizard.WizardsPlugin;

public class HibernateToolsInstaller {
	
	private boolean done = false;
	
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
									ForgeHelper.getDefaultRuntime().sendCommand("set VERBOSE true");
									String str = ForgeHelper.getDefaultRuntime().sendCommand("forge install-plugin hibernate-tools");
									System.out.println(str);
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
