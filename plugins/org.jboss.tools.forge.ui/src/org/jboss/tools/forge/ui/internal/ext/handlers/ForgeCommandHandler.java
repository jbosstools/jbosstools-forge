/**
 * Copyright (c) Red Hat, Inc., contributors and others 2013 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.forge.ui.internal.ext.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.jboss.tools.forge.core.furnace.FurnaceRuntime;
import org.jboss.tools.forge.core.preferences.ForgeCorePreferences;
import org.jboss.tools.forge.core.runtime.ForgeRuntime;
import org.jboss.tools.forge.core.runtime.ForgeRuntimeState;
import org.jboss.tools.forge.ui.internal.ForgeUIPlugin;
import org.jboss.tools.forge.ui.internal.ext.dialog.UICommandListDialog;
import org.jboss.tools.forge.ui.internal.part.ForgeConsoleView;
import org.jboss.tools.forge.ui.util.ForgeHelper;

public class ForgeCommandHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		saveAll(event);
		ForgeRuntime runtime = ForgeCorePreferences.INSTANCE.getDefaultRuntime();
		if (runtime == FurnaceRuntime.INSTANCE) {
			handleFurnace(event);
		} else {
			startForgeRuntime(runtime);
		}
		return null;
	}

	private void saveAll(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		if (window != null) {
			IWorkbenchPage page = window.getActivePage();
			if (page != null) {
				boolean confirm = false;
				page.saveAllEditors(confirm);
			}
		}
	}

	private void handleFurnace(ExecutionEvent event) {
		try {
			ForgeConsoleView forgeConsoleView = ForgeHelper.findForgeConsoleView();
			if (forgeConsoleView != null && forgeConsoleView.isShowing()) {
				ForgeHelper.showRuntime(FurnaceRuntime.INSTANCE);
			}
			final IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
			if (!ForgeRuntimeState.RUNNING.equals(FurnaceRuntime.INSTANCE.getState())) {
				Job job = ForgeHelper.createStartRuntimeJob(FurnaceRuntime.INSTANCE);
				job.addJobChangeListener(new JobChangeAdapter() {
					@Override
					public void done(IJobChangeEvent event) {
						Display.getDefault().asyncExec(new Runnable() {
							@Override
							public void run() {
								new UICommandListDialog(window).open();
							}
						});
					}
				});
				job.schedule();
			} else {
				new UICommandListDialog(window).open();
			}
		} catch (Exception e) {
			ForgeUIPlugin.log(e);
		}
	}

	private void startForgeRuntime(ForgeRuntime runtime) {
		ForgeHelper.showForgeConsole(runtime);
		if (!ForgeRuntimeState.RUNNING.equals(runtime.getState())) {
			ForgeHelper.start(runtime);
		}
	}

}
