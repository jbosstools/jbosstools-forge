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
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.tools.forge.ext.core.FurnaceService;
import org.jboss.tools.forge.ui.ext.ForgeUIPlugin;
import org.jboss.tools.forge.ui.ext.context.UISelectionImpl;
import org.jboss.tools.forge.ui.ext.wizards.ForgeWizardPage;

public class JavaPackageChooserControlBuilder extends ControlBuilder {

	@Override
	public Control build(final ForgeWizardPage page,
			final InputComponent<?, Object> input, final Composite parent) {
		// Create the label
		Label label = new Label(parent, SWT.NULL);
		label.setText(input.getLabel() == null ? input.getName() : input
				.getLabel());

		Composite container = new Composite(parent, SWT.NULL);
		container.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 2;
		layout.verticalSpacing = 9;
		layout.marginWidth = 0;
		layout.marginHeight = 0;

		final Text containerText = new Text(container, SWT.BORDER | SWT.SINGLE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		containerText.setLayoutData(gd);

		// Set Default Value
		final ConverterFactory converterFactory = FurnaceService.INSTANCE
				.getConverterFactory();
		if (converterFactory != null) {
			Converter<Object, String> converter = converterFactory
					.getConverter(input.getValueType(), String.class);
			String value = converter
					.convert(InputComponents.getValueFor(input));
			containerText.setText(value == null ? "" : value);
		}

		containerText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				String text = containerText.getText();
				if (text != null) {
					InputComponents.setValueFor(converterFactory, input, text);
				}
			}
		});

		Button button = new Button(container, SWT.PUSH);
		button.setText("Browse...");
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ElementListSelectionDialog dialog = new ElementListSelectionDialog(
						parent.getShell(), new JavaElementLabelProvider());
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
		});
		return containerText;
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
