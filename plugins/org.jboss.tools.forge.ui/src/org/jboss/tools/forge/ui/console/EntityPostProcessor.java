package org.jboss.tools.forge.ui.console;

import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ISetSelectionTarget;
import org.jboss.tools.forge.ui.ForgeUIPlugin;


public class EntityPostProcessor implements ForgeCommandPostProcessor {

	@Override
	public void postProcess(Map<String, String> commandDetails) {
		try {
			String crn = commandDetails.get("crn");
			IFile file = ForgeCommandPostProcessorHelper.getFile(crn);
			if (file == null) return;
			IJavaElement javaElement = JavaCore.create(file);
			if (javaElement != null && javaElement.getElementType() == IJavaElement.COMPILATION_UNIT) {
				IWorkbenchPage workbenchPage = ForgeCommandPostProcessorHelper.getActiveWorkbenchPage();
				IViewPart projectExplorer = workbenchPage.findView("org.eclipse.ui.navigator.ProjectExplorer");
				if (projectExplorer != null && projectExplorer instanceof ISetSelectionTarget) {
					((ISetSelectionTarget)projectExplorer).selectReveal(new StructuredSelection(file));
				} 
				IViewPart packageExplorer = workbenchPage.findView("org.eclipse.jdt.ui.PackageExplorer"); 
				if (packageExplorer == null && projectExplorer == null) {
					packageExplorer = workbenchPage.showView("org.eclipse.jdt.ui.PackageExplorer");
				} 
				if (packageExplorer != null && packageExplorer instanceof ISetSelectionTarget) {
					((ISetSelectionTarget)packageExplorer).selectReveal(new StructuredSelection(file));
				}
			}
		} catch (PartInitException e) {
			ForgeUIPlugin.log(e);
		}
	}
	
}
