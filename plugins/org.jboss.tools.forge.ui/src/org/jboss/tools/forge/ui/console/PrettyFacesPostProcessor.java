package org.jboss.tools.forge.ui.console;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.ISetSelectionTarget;
import org.jboss.tools.forge.importer.ProjectConfigurationUpdater;
import org.jboss.tools.forge.ui.ForgeUIPlugin;

public class PrettyFacesPostProcessor implements ForgeCommandPostProcessor {

	@Override
	public void postProcessCommand(String command, String output) {
		IProject project = ForgeCommandPostProcessorHelper.getProject(command);
		int index = output.lastIndexOf("***SUCCESS*** Installed [com.ocpsoft.prettyfaces] successfully.");
		if (index == -1) return;
		String str = output.substring(0, index - 1);
		index = str.lastIndexOf("Wrote ");
		if (index == -1) return;
		if (index + 6 > str.length()) return;
		str = str.substring(index + 6);
		String projectLocation = project.getLocation().toString();
		index = str.lastIndexOf(projectLocation);
		if (index != 0) return;
		str = str.substring(projectLocation.length());
		IFile file = project.getFile(str);
		if (file == null) return;
		Object objectToSelect = file;
		new ProjectConfigurationUpdater(project).updateProject();
		try {
			IWorkbenchPage workbenchPage = ForgeCommandPostProcessorHelper.getActiveWorkbenchPage();
			IDE.openEditor(workbenchPage, file);
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
