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
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
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
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		openWizardDialog(window);
		return null;
	}

	public void openWizardDialog(IWorkbenchWindow window) {
		ForgeRuntime runtime = ForgeCorePreferences.INSTANCE.getDefaultRuntime();
		if (runtime != FurnaceRuntime.INSTANCE) {
			startForgeRuntime(runtime);
		} else {
			try {
				ForgeConsoleView forgeConsoleView = ForgeHelper.findForgeConsoleView();
				if (forgeConsoleView != null && forgeConsoleView.isShowing()) {
					ForgeHelper.showRuntime(FurnaceRuntime.INSTANCE);
				}
				if (saveCurrentEditor(window)) {
					if (!ForgeRuntimeState.RUNNING.equals(FurnaceRuntime.INSTANCE.getState())) {
						Job job = ForgeHelper.createStartRuntimeJob(FurnaceRuntime.INSTANCE);
						job.addJobChangeListener(new JobChangeAdapter() {
							@Override
							public void done(IJobChangeEvent event) {
								Display.getDefault().asyncExec(() -> new UICommandListDialog(window).open());
							}
						});
						job.schedule();
					} else {
						new UICommandListDialog(window).open();
					}
				}
			} catch (Exception e) {
				ForgeUIPlugin.log(e);
			}
		}
	}

	private boolean saveCurrentEditor(IWorkbenchWindow window) throws ExecutionException {
		boolean saveOnCommandMenu = ForgeCorePreferences.INSTANCE.isSaveOnCommandMenu();
		boolean result = true;
		IWorkbenchPage page = window.getActivePage();
		if (page != null) {
			IEditorPart editor = page.getActiveEditor();
			if (editor != null && editor.isDirty()) {
				if (!saveOnCommandMenu) {
					MessageDialogWithToggle dialog = MessageDialogWithToggle.openYesNoQuestion(window.getShell(),
							"Save Resource",
							"'" + editor.getTitle()
									+ "' has been modified. Save changes?\n (For Forge to access latest changes you need to save)",
							"Always save before opening the command dialog", false, null, null);
					// No == 3, Cancel == 1, Yes == 2
					switch (dialog.getReturnCode()) {
					case 1:
						result = false;
						break;
					case 2:
						saveOnCommandMenu = true;
						// fall-back
					default:
						if (dialog.getToggleState()) {
							ForgeCorePreferences.INSTANCE.setSaveOnCommandMenu(true);
						}
						break;
					}
				}
				if (saveOnCommandMenu) {
					result = page.saveEditor(editor, false);
				}
			}
		}
		return result;
	}

	private void startForgeRuntime(ForgeRuntime runtime) {
		ForgeHelper.showForgeConsole(runtime);
		if (!ForgeRuntimeState.RUNNING.equals(runtime.getState())) {
			ForgeHelper.start(runtime);
		}
	}

}
