package org.jboss.tools.forge.ui.util;

import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.forge.ui.part.ForgeView;

public class ForgeHelper {
	
	public static ForgeView getForgeView() {
		IWorkbench workbench = PlatformUI.getWorkbench();
		if (workbench == null) return null;
		IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
		if (workbenchWindow == null) return null;
		IWorkbenchPage workbenchPage = workbenchWindow.getActivePage();
		if (workbenchPage == null) return null;
		IViewPart viewPart = workbenchPage.findView(ForgeView.ID);
		if (viewPart != null && viewPart instanceof ForgeView) {
			return (ForgeView)viewPart;
		} else {
			return null;
		}
	}

}
