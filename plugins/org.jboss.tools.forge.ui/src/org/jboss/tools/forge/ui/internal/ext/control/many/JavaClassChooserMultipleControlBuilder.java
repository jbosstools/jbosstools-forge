/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.tools.forge.ui.internal.ext.control.many;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.tools.forge.ui.internal.ForgeUIPlugin;
import org.jboss.tools.forge.ui.internal.ext.util.BusyIndicatorRunnableContext;
import org.jboss.tools.forge.ui.internal.ext.wizards.ForgeWizardPage;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class JavaClassChooserMultipleControlBuilder extends
		AbstractListButtonControl {

	@Override
	protected void addButtonPressed(ForgeWizardPage page,
			InputComponent<?, ?> input, List containerList) {
		IRunnableContext context = new BusyIndicatorRunnableContext();
		IJavaSearchScope scope = SearchEngine.createWorkspaceScope();
		int style = IJavaElementSearchConstants.CONSIDER_ALL_TYPES;
		try {
			SelectionDialog dialog = JavaUI.createTypeDialog(page.getShell(),
					context, scope, style, true);
			dialog.setTitle("Type Selection");
			dialog.setMessage("Choose type name:");
			if (dialog.open() == Window.OK) {
				for (Object item : dialog.getResult()) {
					IType res = (IType) item;
					containerList.add(res.getFullyQualifiedName('.'));
				}
				updateItems(input, containerList);
			}
		} catch (JavaModelException ex) {
			ForgeUIPlugin.log(ex);
		}
	}

	@Override
	protected Class<String> getProducedType() {
		return String.class;
	}

	@Override
	protected String getSupportedInputType() {
		return InputType.JAVA_CLASS_PICKER;
	}

}
