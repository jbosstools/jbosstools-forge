/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.tools.forge.ui.ext.control;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.tools.forge.ui.ext.ForgeUIPlugin;
import org.jboss.tools.forge.ui.ext.context.UISelectionImpl;
import org.jboss.tools.forge.ui.ext.wizards.ForgeWizardPage;

public class JavaPackageChooserControlBuilder extends AbstractTextButtonControl {

	@Override
	protected void browseButtonPressed(ForgeWizardPage page,
			InputComponent<?, Object> input, Text containerText) {
		UISelectionImpl<?> initialSelection = page.getUIContext()
				.getInitialSelection();
		final IJavaProject project;
		if (initialSelection != null) {
			IResource resource = initialSelection.getResource();
			if (resource != null) {
				project = JavaCore.create(resource.getProject());
			} else {
				project = null;
			}
		} else {
			project = null;
		}
		if (project == null) {
			MessageDialog.openError(page.getShell(), "No project selected",
					"No project was selected");
			return;
		}
		int style = IJavaElementSearchConstants.CONSIDER_REQUIRED_PROJECTS;
		try {
			SelectionDialog dialog = JavaUI.createPackageDialog(
					page.getShell(), project, style, containerText.getText());
			dialog.setTitle("Package Selection");
			dialog.setMessage("Select a package.");
			if (dialog.open() == Window.OK) {
				IPackageFragment res = (IPackageFragment) dialog.getResult()[0];
				containerText.setText(res.getElementName());
			}
		} catch (JavaModelException ex) {
			ForgeUIPlugin.log(ex);
		}
	}

	@Override
	public void setEnabled(Control control, boolean enabled) {
		Composite container = (Composite) control;
		for (Control childControl : container.getChildren()) {
			childControl.setEnabled(enabled);
		}
	}

	@Override
	protected Class<String> getProducedType() {
		return String.class;
	}

	@Override
	protected InputType getSupportedInputType() {
		return InputType.JAVA_PACKAGE_PICKER;
	}

	@Override
	protected Class<?>[] getSupportedInputComponentTypes() {
		return new Class<?>[] { UIInput.class };
	}
}
