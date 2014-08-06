/**
 * Copyright (c) Red Hat, Inc., contributors and others 2013 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.forge.ui.internal.part;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.forge.core.runtime.ForgeRuntime;
import org.jboss.tools.forge.core.runtime.ForgeRuntimeState;
import org.jboss.tools.forge.ui.internal.console.ForgeConsole;
import org.jboss.tools.forge.ui.internal.console.ForgeConsoleManager;


public class SelectionSynchronizer implements ISelectionListener {
	
	private ForgeRuntime runtime;

	private IEditorPart selectedPart;
	
	private IPartListener partListener = new IPartListener() {		
		@Override public void partOpened(IWorkbenchPart part) {}		
		@Override public void partDeactivated(IWorkbenchPart part) {}		
		@Override public void partClosed(IWorkbenchPart part) {}		
		@Override public void partActivated(IWorkbenchPart part) {}
		@Override public void partBroughtToTop(IWorkbenchPart part) {
			if (!(part instanceof IEditorPart) || part == selectedPart) return;
			selectedPart = (IEditorPart)part;
			IEditorInput editorInput = selectedPart.getEditorInput();
			if (!(editorInput instanceof IFileEditorInput)) return;
			IFile file = ((IFileEditorInput)editorInput).getFile();
			if (file == null) return;
			String path = file.getLocation().toOSString();
			if (ForgeRuntimeState.RUNNING.equals(getRuntime().getState())) {
				ForgeConsole console = ForgeConsoleManager.INSTANCE.getConsole(getRuntime());
				console.goToPath(path);
			}
		}		
	};
	
	public SelectionSynchronizer(ForgeRuntime runtime) {
		this.runtime = runtime;
	}
	
	public void setEnabled(boolean enabled) {
		if (enabled) {
			getPartService().addPartListener(partListener);
		} else  {
			getPartService().removePartListener(partListener);
		}
	}
	
	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (!(part instanceof IEditorPart) || part == selectedPart) return;
		selectedPart = (IEditorPart)part;
		if (getPartService().getActivePart() == part) return;
		IEditorInput editorInput = selectedPart.getEditorInput();
		if (!(editorInput instanceof IFileEditorInput)) return;
		IFile file = ((IFileEditorInput)editorInput).getFile();
		if (file == null) return;
		String path = file.getLocation().toOSString();
		if (ForgeRuntimeState.RUNNING.equals(getRuntime().getState())) {
			ForgeConsole console = ForgeConsoleManager.INSTANCE.getConsole(getRuntime());
			console.goToPath(path);
		}
	}
	
	private IPartService getPartService() {
		IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		return activeWorkbenchWindow.getPartService();
	}
	
	private ForgeRuntime getRuntime() {
		return runtime;
	}
	
}
