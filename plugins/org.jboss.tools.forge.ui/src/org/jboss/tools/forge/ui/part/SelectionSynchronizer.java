package org.jboss.tools.forge.ui.part;

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
import org.jboss.tools.forge.core.preferences.ForgeRuntimesPreferences;
import org.jboss.tools.forge.core.process.ForgeRuntime;


public class SelectionSynchronizer implements ISelectionListener {

	private ForgeView forgeView;
	private IEditorPart selectedPart;
	private IPartService partService;
	
	private IPartListener partListener = new IPartListener() {
		
		@Override
		public void partOpened(IWorkbenchPart part) {}
		
		@Override
		public void partDeactivated(IWorkbenchPart part) {}
		
		@Override
		public void partClosed(IWorkbenchPart part) {}
		
		@Override
		public void partBroughtToTop(IWorkbenchPart part) {
			if (!(part instanceof IEditorPart) || part == selectedPart) return;
			selectedPart = (IEditorPart)part;
			IEditorInput editorInput = selectedPart.getEditorInput();
			if (!(editorInput instanceof IFileEditorInput)) return;
			IFile file = ((IFileEditorInput)editorInput).getFile();
			if (file == null) return;
			String path = file.getLocation().toOSString();
			if (path.indexOf(' ') != -1) {
				path = '\"' + path + '\"';
			}
			ForgeRuntime forgeRuntime = ForgeRuntimesPreferences.INSTANCE.getDefaultRuntime();
			if (forgeRuntime != null && ForgeRuntime.STATE_RUNNING.equals(forgeRuntime.getState())) {
				forgeRuntime.sendInput("pick-up " + path + "\n");
			}
		}
		
		@Override
		public void partActivated(IWorkbenchPart part) {
			// Ignore
		}
	};
	
	public SelectionSynchronizer(ForgeView forgeView) {
		this.forgeView = forgeView;
		IWorkbenchWindow workbenchWindow = forgeView.getSite().getWorkbenchWindow();
		partService = workbenchWindow.getPartService();
	}
	
	public void setEnabled(boolean enabled) {
		if (enabled) {
			partService.addPartListener(partListener);
		} else  {
			partService.removePartListener(partListener);
		}
	}
	
	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (!(part instanceof IEditorPart) || part == selectedPart) return;
		selectedPart = (IEditorPart)part;
		IWorkbenchWindow workbenchWindow = forgeView.getSite().getWorkbenchWindow();
		if (workbenchWindow.getPartService().getActivePart() == part) return;
		IEditorInput editorInput = selectedPart.getEditorInput();
		if (!(editorInput instanceof IFileEditorInput)) return;
		IFile file = ((IFileEditorInput)editorInput).getFile();
		if (file == null) return;
		String path = file.getLocation().toOSString();
		if (path.indexOf(' ') != -1) {
			path = '\"' + path + '\"';
		}
		ForgeRuntime forgeRuntime = ForgeRuntimesPreferences.INSTANCE.getDefaultRuntime();
		if (forgeRuntime != null && ForgeRuntime.STATE_RUNNING.equals(forgeRuntime.getState())) {
			forgeRuntime.sendInput("pick-up " + path + "\n");
		}
	}
	
}
