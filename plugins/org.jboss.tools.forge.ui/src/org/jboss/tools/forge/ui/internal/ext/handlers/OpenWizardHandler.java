/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.tools.forge.ui.internal.ext.handlers;

import java.io.File;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.tools.forge.core.furnace.FurnaceRuntime;
import org.jboss.tools.forge.core.runtime.ForgeRuntimeState;
import org.jboss.tools.forge.ui.internal.ForgeUIPlugin;
import org.jboss.tools.forge.ui.internal.ext.dialog.UICommandListDialog;
import org.jboss.tools.forge.ui.internal.ext.dialog.WizardDialogHelper;
import org.jboss.tools.forge.ui.util.ForgeHelper;

/**
 * A handler to open Forge wizards
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class OpenWizardHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// Parameters
		final String wizardName = event
				.getParameter("org.jboss.tools.forge.ui.openWizard.wizardName");
		final String wizardTitle = event
				.getParameter("org.jboss.tools.forge.ui.openWizard.wizardTitle");
		final String path = event
				.getParameter("org.jboss.tools.forge.ui.command.openWizard.wizardPath");

		try {
			final IWorkbenchWindow window = HandlerUtil
					.getActiveWorkbenchWindowChecked(event);
			if (!ForgeRuntimeState.RUNNING.equals(FurnaceRuntime.INSTANCE
					.getState())) {
				Job job = ForgeHelper
						.createStartRuntimeJob(FurnaceRuntime.INSTANCE);
				job.addJobChangeListener(new JobChangeAdapter() {
					@Override
					public void done(IJobChangeEvent event) {
						Display.getDefault().asyncExec(new Runnable() {
							@Override
							public void run() {
								openWizard(wizardName, wizardTitle, path,
										window);
							}
						});
					}
				});
				job.schedule();
			} else {
				openWizard(wizardName, wizardTitle, path, window);
			}
		} catch (Exception e) {
			ForgeUIPlugin.log(e);
		}
		return null;
	}

	private void openWizard(String wizardName, String wizardTitle, String path,
			IWorkbenchWindow window) {
		IStructuredSelection currentSelection;
		if (path == null) {
			currentSelection = UICommandListDialog.getCurrentSelection(window);
		} else {
			currentSelection = new StructuredSelection(new File(path));
		}
		WizardDialogHelper helper = new WizardDialogHelper(window.getShell(),
				currentSelection);
		UICommand command = helper.getCommand(wizardName);
		helper.openWizard(wizardTitle == null ? wizardName : wizardTitle,
				command);
	}
}
