package org.jboss.tools.forge.ui.action;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.part.ShowInContext;
import org.jboss.tools.forge.ui.part.ForgeView;

public class ShowInForgeDelegate implements IViewActionDelegate {
	
	private ForgeView forgeView = null;
	
	@Override
	public void run(IAction action) {
		if (forgeView != null) {
			forgeView.show(new ShowInContext(null, forgeView.getSelection()));
		}
	}
	
	@Override
	public void selectionChanged(IAction action, ISelection selection) {
	}

	@Override
	public void init(IViewPart view) {
		if (view != null && view instanceof ForgeView) {
			forgeView = (ForgeView)view;
		}
	}

}
