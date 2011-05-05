package org.jboss.tools.seam.forge.console;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.ISetSelectionTarget;
import org.eclipse.ui.texteditor.ITextEditor;
import org.jboss.tools.seam.forge.ForgePlugin;
import org.jboss.tools.seam.forge.importer.ProjectImporter;

public class CommandRecorder implements IDocumentListener {
	
	private StringBuffer buffer = new StringBuffer();
	private String beforePrompt = null;
	private String currentPrompt = null;
	private String currentCommand = null;

	@Override
	public void documentAboutToBeChanged(DocumentEvent event) {
	}

	@Override
	public void documentChanged(DocumentEvent event) {
		if (event.getLength() == 1 && "".equals(event.getText())) {
			buffer.setLength(buffer.length() - 1);
		} else {
			buffer.append(event.getText());
			String newPrompt = getNewPrompt();
			if (newPrompt != null) {
				currentPrompt = newPrompt;
				beforePrompt = buffer.substring(0, buffer.length() - newPrompt.length());
				buffer.setLength(0);
				if (currentCommand != null) {
					postProcessCurrentCommand();
					currentCommand = null;
				}
			} else {
				if (currentPrompt != null) {
					String newCommand = getNewCommand();
					if (newCommand != null) {
						currentCommand = newCommand;
					}
				}
			}
		}
	}
	
	private String getNewPrompt() {
		int lastLineBreak = buffer.lastIndexOf("\n");
		if (lastLineBreak == -1) return null;
		String lastLine = buffer.substring(lastLineBreak + 1);
		if (lastLine.length() == 0) return null;
		if (lastLine.charAt(0) != '[') return null;
		int rightBracketIndex = lastLine.indexOf(']');
		if (rightBracketIndex == -1) return null;
		return lastLine.endsWith("$ ") ? lastLine : null;
	}
	
	private String getNewCommand() {
		String candidateCommand = buffer.toString();
		if ("pwd".equals(candidateCommand)) {
			return "pwd";
		} else if ("new-project".equals(candidateCommand)) {
			return "new-project";
		} else if ("persistence".equals(candidateCommand)) {
			return "persistence";
		} else if ("entity".equals(candidateCommand)) {
			return "entity";
		} else if ("field".equals(candidateCommand)) {
			return "field";
		} else if ("prettyfaces".equals(candidateCommand)) {
			return "prettyfaces";
		} else if ("build".equals(candidateCommand)) {
			return "build";
		} else {
			return null;
		}
	}
	
	private void postProcessCurrentCommand() {
		IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage workbenchPage = workbenchWindow.getActivePage();
		String projectName = currentPrompt.substring(1, currentPrompt.indexOf(']'));
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		if (project != null) {
			try {
				project.refreshLocal(IResource.DEPTH_INFINITE, null);
			} catch (CoreException e) {
				ForgePlugin.log(e);
			}
		}
		if ("pwd".equals(currentCommand)) {
			// do nothing
		} else if ("new-project".equals(currentCommand)) {
			int index = beforePrompt.lastIndexOf("***SUCCESS*** Created project [");
			if (index == -1) return;
			if (index + 31 > beforePrompt.length()) return;
			String str = beforePrompt.substring(index + 31);
			index = str.lastIndexOf("] in new working directory [");
			if (index == -1) return;
			if (index + 28 > str.length()) return;
			str = str.substring(index + 28);
			index = str.indexOf("]");
			if (index == -1) return;
			String projectPath = str.substring(0, index);
			index = projectPath.lastIndexOf('/');
			String projectDirName = projectPath.substring(index + 1);
			String projectBaseDirPath = projectPath.substring(0, index);
			ProjectImporter importer = new ProjectImporter(projectBaseDirPath, projectDirName);
			importer.importProject();
		} else if ("persistence".equals(currentCommand)) {
			int index = beforePrompt.lastIndexOf("***SUCCESS*** Installed [forge.spec.jpa] successfully.\nWrote ");
			if (index == -1) return;
			try {
				IFile file = project.getFile("/src/main/resources/META-INF/persistence.xml");
				if (file == null) return;
				Object objectToSelect = file;
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
				ForgePlugin.log(e);
			}
		} else if ("entity".equals(currentCommand)) {
			int index = beforePrompt.lastIndexOf("Picked up type <JavaResource>: ");
			if (index == -1) return;
			if (index + 31 > beforePrompt.length() -1) return;
			String entityName = beforePrompt.substring(index + 31, beforePrompt.length() - 1).replace('.', '/');
			try {
				IFile file = project.getFile("/src/main/java/" + entityName + ".java");
				if (file == null) return;
				Object objectToSelect = file;
				IDE.openEditor(workbenchPage, file);
				IJavaElement javaElement = JavaCore.create(file);
				if (javaElement != null && javaElement.getElementType() == IJavaElement.COMPILATION_UNIT) {
					try {
						objectToSelect = ((ICompilationUnit)javaElement).getTypes()[0];
					} catch (JavaModelException e) {
						ForgePlugin.log(e);
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
				ForgePlugin.log(e);
			}
		} else if ("field".equals(currentCommand)) {
			try {
				int index = beforePrompt.lastIndexOf("Added field to ");
				if (index == -1) return;
				if (index + 15 > beforePrompt.length()) return;
				String str = beforePrompt.substring(index + 15);
				index = str.indexOf(':');
				if (index == -1) return;
				String entityName = str.substring(0, index);
				str = str.substring(index);
				index = str.lastIndexOf(';');
				if (index == -1) return;
				str = str.substring(0, index);
				index = str.lastIndexOf(' ');
				if (index == -1) return;
				String fieldName = str.substring(index + 1);
				IFile file = project.getFile("/src/main/java/" + entityName.replace('.', '/') + ".java");
				if (file == null) return;
				IEditorPart editorPart = IDE.openEditor(workbenchPage, file);
				IJavaElement javaElement = JavaCore.create(file);
				if (javaElement != null && javaElement.getElementType() == IJavaElement.COMPILATION_UNIT) {
					try {
						IType type = ((ICompilationUnit)javaElement).getTypes()[0];
						IField field = type.getField(fieldName);
						if (field != null) {
							ISourceRange sourceRange = field.getSourceRange();
							if (sourceRange != null && editorPart != null && editorPart instanceof ITextEditor) {
								((ITextEditor)editorPart).selectAndReveal(sourceRange.getOffset(), sourceRange.getLength());
							}
						}
					} catch (JavaModelException e) {
						ForgePlugin.log(e);
					}
				}
			} catch (PartInitException e) {
				ForgePlugin.log(e);
			}
		} else if ("prettyfaces".equals(currentCommand)) {
			int index = beforePrompt.lastIndexOf("***SUCCESS*** Installed [com.ocpsoft.prettyfaces] successfully.");
			if (index == -1) return;
			String str = beforePrompt.substring(0, index - 1);
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
			try {
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
				ForgePlugin.log(e);
			}
		} else if ("build".equals(currentCommand)) {
			
		} else {
			
		}
		try {
			workbenchPage.showView("org.jboss.tools.seam.forge.console").setFocus();
		} catch (PartInitException e) {
			ForgePlugin.log(e);
		}
	}

}
