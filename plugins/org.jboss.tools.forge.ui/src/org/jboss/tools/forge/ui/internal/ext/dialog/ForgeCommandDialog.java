/**
 * Copyright (c) Red Hat, Inc., contributors and others 2004 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.forge.ui.internal.ext.dialog;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.ProgressMonitorPart;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.jboss.tools.forge.ui.internal.ext.wizards.ForgeWizard;

/**
 * @author <a href="lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ForgeCommandDialog extends WizardDialog {

	public ForgeCommandDialog(Shell shell, IWizard wizard) {
		super(shell, wizard);
	}

	private InterruptableProgressMonitor progressMonitor;
	private boolean running = false;

	@Override
	protected InterruptableProgressMonitor getProgressMonitor() {

		if (progressMonitor == null)
			progressMonitor = new InterruptableProgressMonitor(
					super.getProgressMonitor());

		if (getWizard() instanceof ForgeWizard)
			((ForgeWizard) getWizard()).setProgressMonitor(progressMonitor);

		return progressMonitor;
	}

	@Override
	protected ProgressMonitorPart createProgressMonitorPart(
			Composite composite, GridLayout layout) {
		return new ForgeProgressMonitorPart(composite, layout, this);
	}

	@Override
	public void run(boolean fork, boolean cancelable,
			IRunnableWithProgress runnable) throws InvocationTargetException,
			InterruptedException {
		this.running = true;
		super.run(fork, cancelable, runnable);
	}

	@Override
	protected void cancelPressed() {
		if (getProgressMonitor() != null && isRunning()) {
			if (getProgressMonitor().isPreviouslyCancelled()) {
				getProgressMonitor().setCanceled(true);
				super.cancelPressed();
			} else {
				getProgressMonitor().setCanceled(true);
			}
		} else {
			super.cancelPressed();
		}
	}

	public boolean isRunning() {
		return running;
	}
}
