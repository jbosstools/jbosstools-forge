package org.jboss.tools.forge.ui.console;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.ITextEditor;
import org.jboss.tools.forge.ui.ForgeUIPlugin;

public class FieldPostProcessor implements ForgeCommandPostProcessor {

	@Override
	public void postProcessCommand(String command, String output) {
		try {
			int index = output.lastIndexOf("Added field to ");
			if (index == -1) return;
			if (index + 15 > output.length()) return;
			String str = output.substring(index + 15);
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
			IProject project = ForgeCommandPostProcessorHelper.getProject(command);
			if (project == null) return;
			IFile file = project.getFile("/src/main/java/" + entityName.replace('.', '/') + ".java");
			if (file == null) return;
			IJavaElement javaElement = JavaCore.create(file);
			if (javaElement != null && javaElement.getElementType() == IJavaElement.COMPILATION_UNIT) {
				try {
					IType type = ((ICompilationUnit)javaElement).getTypes()[0];
					IField field = type.getField(fieldName);
					if (field != null) {
						ISourceRange sourceRange = field.getSourceRange();
						IWorkbenchPage workbenchPage = ForgeCommandPostProcessorHelper.getActiveWorkbenchPage();
						IEditorPart editorPart = IDE.openEditor(workbenchPage, file);
						if (sourceRange != null && editorPart != null && editorPart instanceof ITextEditor) {
							((ITextEditor)editorPart).selectAndReveal(sourceRange.getOffset(), sourceRange.getLength());
						}
					}
				} catch (JavaModelException e) {
					ForgeUIPlugin.log(e);
				}
			}
		} catch (PartInitException e) {
			ForgeUIPlugin.log(e);
		}
	}

}
