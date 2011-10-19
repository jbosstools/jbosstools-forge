package org.jboss.tools.forge.ui.console;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.ISetSelectionTarget;
import org.jboss.tools.forge.ui.ForgeUIPlugin;

public class PickUpPostProcessor implements ForgeCommandPostProcessor {

	@Override
	public void postProcessCommand(final String command, final String output) {
		IProject project = ForgeCommandPostProcessorHelper.getProject(command);
		if (project == null) return;
		int index = output.lastIndexOf("Picked up type <JavaResource>: ");
		if (index == -1) return;
		if (index + 31 > output.length() -1) return;
		String entityName = output.substring(index + 31, output.length() - 1).replace('.', '/');
		try {
			IFile file = project.getFile("/src/main/java/" + entityName + ".java");
			if (file == null) return;
			Object objectToSelect = file;
			IWorkbenchPage workbenchPage = ForgeCommandPostProcessorHelper.getActiveWorkbenchPage();
			if (workbenchPage == null) return;
			IDE.openEditor(workbenchPage, file);
			IJavaElement javaElement = JavaCore.create(file);
			if (javaElement != null && javaElement.getElementType() == IJavaElement.COMPILATION_UNIT) {
				try {
					objectToSelect = ((ICompilationUnit)javaElement).getTypes()[0];
				} catch (JavaModelException e) {
					ForgeUIPlugin.log(e);
				}
			}
			IViewPart projectExplorer = workbenchPage.findView("org.eclipse.ui.navigator.ProjectExplorer");
			if (projectExplorer != null && projectExplorer instanceof ISetSelectionTarget) {
				((ISetSelectionTarget)projectExplorer).selectReveal(new StructuredSelection(objectToSelect));
			} 
			IViewPart packageExplorer = workbenchPage.findView("org.eclipse.jdt.ui.PackageExplorer"); 
			if (packageExplorer == null && projectExplorer == null) {
				packageExplorer = workbenchPage.showView("org.eclipse.jdt.ui.PackageExplorer");
			} 
			if (packageExplorer != null && packageExplorer instanceof ISetSelectionTarget) {
				((ISetSelectionTarget)packageExplorer).selectReveal(new StructuredSelection(objectToSelect));
			}
		} catch (PartInitException e) {
			ForgeUIPlugin.log(e);
		}
	}

	
}
