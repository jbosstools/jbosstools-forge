package org.jboss.tools.seam.forge.action;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.jboss.tools.seam.forge.runtime.Manager;

public class StartDelegate implements IViewActionDelegate {
	
	@Override
	public void run(IAction action) {
		Manager.INSTANCE.startForge();
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(IViewPart view) {
		// TODO Auto-generated method stub
		
	}

}
