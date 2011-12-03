package org.jboss.tools.forge.ui.console;

import java.util.Map;

import org.eclipse.core.resources.IFile;
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
	public void postProcess(Map<String, String> commandDetails) {
		try {
			String crn = commandDetails.get("crn");
			String par = commandDetails.get("par").trim();
			IFile file = ForgeCommandPostProcessorHelper.getFile(crn);
			if (file == null) return;
			IJavaElement javaElement = JavaCore.create(file);
			if (javaElement != null && javaElement.getElementType() == IJavaElement.COMPILATION_UNIT) {
				try {
					IType type = ((ICompilationUnit)javaElement).getTypes()[0];
					IField field = type.getField(par);
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
