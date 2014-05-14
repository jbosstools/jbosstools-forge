package org.jboss.tools.forge.ui.internal.actions;

import java.net.URL;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.rse.subsystems.files.core.subsystems.IRemoteFile;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.forge.core.runtime.ForgeRuntime;
import org.jboss.tools.forge.core.runtime.ForgeRuntimeState;
import org.jboss.tools.forge.ui.internal.ForgeUIPlugin;

public class GoToAction extends Action implements ISelectionListener {
	
	private ISelection selection;
	private ForgeRuntime runtime;
	
	public GoToAction(ForgeRuntime runtime) {
		super();
		this.runtime = runtime;
		setImageDescriptor(createImageDescriptor());
		getSelectionService().addPostSelectionListener(this);
	}

	@Override
	public void run() {
		goToSelection();
	}
	
	@Override
	protected void finalize() {
		getSelectionService().removeSelectionListener(this);
	}
	
	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection newSelection) {
		selection = newSelection;
	}

	public boolean isEnabled() {
		return ForgeRuntimeState.RUNNING.equals(runtime.getState());
	}

	private ImageDescriptor createImageDescriptor() {
		URL url = ForgeUIPlugin.getDefault().getBundle().getEntry("icons/goto_obj.gif");
		return ImageDescriptor.createFromURL(url);
	}

	public boolean goToSelection() {
		if (selection instanceof IStructuredSelection) {
		    IStructuredSelection ss = (IStructuredSelection)selection;
		    Object first = ss.getFirstElement();
		    if (first instanceof IResource) {
		    	goToPath(((IResource)first).getLocation().toOSString());
		    } else if (first instanceof IJavaElement) {
		    	try {
		    		IResource resource = ((IJavaElement)first).getCorrespondingResource();
		    		if (resource == null) return false;
					goToPath(resource.getLocation().toOSString());
				} catch (JavaModelException e) {
					ForgeUIPlugin.log(e);
					return false;
				}
		    } else if (first instanceof IRemoteFile) {
		    	goToPath(((IRemoteFile)first).getAbsolutePath());
		    }
		    return true;
		} else {
			return false;
		}
	}
	
	private void goToPath(String str) {
		if (str.indexOf(' ') != -1) {
			str = '\"' + str + '\"';
		}
		runtime.sendInput("pick-up " + str + "\n");
	}
	
	private ISelectionService getSelectionService() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService();
	}
	
}
