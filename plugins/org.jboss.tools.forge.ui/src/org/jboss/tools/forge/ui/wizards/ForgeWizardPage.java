/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.tools.forge.ui.wizards;

import java.util.List;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.jboss.forge.ui.UICommand;
import org.jboss.forge.ui.UIInput;
import org.jboss.tools.forge.ui.ForgeUIPlugin;
import org.jboss.tools.forge.ui.context.UIContextImpl;
import org.jboss.tools.forge.ui.control.ControlBuilder;
import org.jboss.tools.forge.ui.control.ControlBuilderRegistry;

/**
 * A Forge Wizard Page
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */
public class ForgeWizardPage extends WizardPage {
	private UICommand ui;
	private UIContextImpl uiContext;

	public ForgeWizardPage(ForgeWizard wizard, UICommand command,
			UIContextImpl contextImpl) {
		super("Page Title");
		setWizard(wizard);
		setTitle("Wizard Page");
		setDescription("A wizard page implementation");
		setImageDescriptor(ForgeUIPlugin.getForgeLogo());
		this.ui = command;
		this.uiContext = contextImpl;
	}

	@Override
	public void createControl(Composite parent) {
		try {
			ui.initializeUI(uiContext);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		List<UIInput<?>> inputs = uiContext.getInputs();

		createControls(parent, inputs);
	}

	@SuppressWarnings("unchecked")
	protected void createControls(Composite parent, List<UIInput<?>> inputs) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 2;
		layout.verticalSpacing = 9;

		for (final UIInput<?> uiInput : inputs) {
			// Create the label
			Label label = new Label(container, SWT.NULL);
			label.setText(uiInput.getLabel() == null ? uiInput.getName()
					: uiInput.getLabel());
			ControlBuilder controlBuilder = ControlBuilderRegistry.INSTANCE
					.getBuilderFor(uiInput);
			controlBuilder.build(this, (UIInput<Object>) uiInput, container);
		}
		setControl(container);
	}

	public UIContextImpl getUIContext() {
		return uiContext;
	}

	public UICommand getUICommand() {
		return ui;
	}

	public void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	@Override
	public ForgeWizard getWizard() {
		return (ForgeWizard) super.getWizard();
	}
}
