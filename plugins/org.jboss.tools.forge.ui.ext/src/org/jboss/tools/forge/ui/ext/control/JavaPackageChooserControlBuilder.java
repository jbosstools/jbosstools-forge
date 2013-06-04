/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.tools.forge.ui.ext.control;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.tools.forge.ui.ext.ForgeUIPlugin;
import org.jboss.tools.forge.ui.ext.context.UISelectionImpl;
import org.jboss.tools.forge.ui.ext.wizards.ForgeWizardPage;

public class JavaPackageChooserControlBuilder extends
		AbstractTextButtonControl {

	@Override
	protected void browseButtonPressed(ForgeWizardPage page,
			InputComponent<?, Object> input, Text containerText) {
		ElementListSelectionDialog dialog = new ElementListSelectionDialog(
				page.getShell(), new JavaElementLabelProvider());
		dialog.setTitle("Package Selection");
		dialog.setMessage("Select a package.");
		IProject project;
		UISelectionImpl<?> initialSelection = page.getUIContext()
				.getInitialSelection();
		if (initialSelection != null) {
			IResource resource = initialSelection.getResource();
			project = (resource != null) ? resource.getProject() : null;
		} else {
			project = null;
		}
		dialog.setElements(getPackageFragments(project).toArray());
		dialog.open();
		Object[] results = dialog.getResult();
		if (results != null && results.length > 0
				&& results[0] instanceof IPackageFragment) {
			IPackageFragment result = (IPackageFragment) results[0];
			containerText.setText(result.getElementName());
		}
	}

	@Override
	public void setEnabled(Control control, boolean enabled) {
		Composite container = (Composite) control;
		for (Control childControl : container.getChildren()) {
			childControl.setEnabled(enabled);
		}
	}

	private List<IPackageFragment> getPackageFragments(IProject project) {
		List<IPackageFragment> result = new ArrayList<IPackageFragment>();
		if (project != null) {
			try {
				IJavaProject javaProject = JavaCore.create(project);
				for (IPackageFragmentRoot root : javaProject
						.getAllPackageFragmentRoots()) {
					if (root.getKind() != IPackageFragmentRoot.K_SOURCE)
						continue;
					for (IJavaElement javaElement : root.getChildren()) {
						addPackageFragments(javaElement, result);
					}
				}
			} catch (JavaModelException e) {
				ForgeUIPlugin.log(e);
			}
		}
		return result;
	}

	private void addPackageFragments(IJavaElement javaElement,
			List<IPackageFragment> list) throws JavaModelException {
		if (javaElement instanceof IPackageFragment) {
			IPackageFragment packageFragment = (IPackageFragment) javaElement;
			if (!packageFragment.isDefaultPackage()) {
				list.add(packageFragment);
			}
			for (IJavaElement child : packageFragment.getChildren()) {
				addPackageFragments(child, list);
			}
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
