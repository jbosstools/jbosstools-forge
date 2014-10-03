/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.tools.forge.ui.internal.ext.handlers;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.viewers.IStructuredSelection;
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
public class RunForgeCommandHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// Parameters
		final String wizardName = event
				.getParameter("org.jboss.tools.forge.ui.runForgeCommand.commandName");
		final String wizardTitle = event
				.getParameter("org.jboss.tools.forge.ui.runForgeCommand.commandTitle");
		final String wizardValues = event
				.getParameter("org.jboss.tools.forge.ui.runForgeCommand.commandValues");
		final Map<String, Object> values;
		if (wizardValues == null) {
			values = null;
		} else {
			values = new HashMap<>();
			for (String entry : wizardValues.split(",")) {
				String[] split = entry.split("=");
				values.put(split[0], split[1]);
			}
		}
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
								openWizard(window, wizardName, wizardTitle,
										values);
							}
						});
					}
				});
				job.schedule();
			} else {
				openWizard(window, wizardName, wizardTitle, values);
			}
		} catch (Exception e) {
			ForgeUIPlugin.log(e);
		}
		return null;
	}

	private void openWizard(IWorkbenchWindow window, String wizardName,
			String wizardTitle, Map<String, ?> values) {
		IStructuredSelection currentSelection = UICommandListDialog
				.getCurrentSelection(window);
		WizardDialogHelper helper = new WizardDialogHelper(window.getShell(),
				currentSelection);
		UICommand command = helper.getCommand(wizardName);
		helper.openWizard(wizardTitle, command, values);
	}
}
