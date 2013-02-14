/*
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.tools.forge.ui.wizards;

import java.util.List;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.jboss.forge.ui.UICommand;
import org.jboss.forge.ui.UICommandMetadata;
import org.jboss.forge.ui.UIInputComponent;
import org.jboss.tools.forge.ui.ForgeUIPlugin;
import org.jboss.tools.forge.ui.context.UIBuilderImpl;
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

	public ForgeWizardPage(Wizard wizard, UICommand command,
			UIContextImpl contextImpl) {
		super("Page Name");
		setWizard(wizard);
		UICommandMetadata id = command.getMetadata();
		setTitle(id.getName());
		setDescription(id.getDescription());
		setImageDescriptor(ForgeUIPlugin.getForgeLogo());
		this.ui = command;
		this.uiContext = contextImpl;
	}

	@Override
	public void createControl(Composite parent) {
		UIBuilderImpl builder = new UIBuilderImpl(uiContext);
		try {
			ui.initializeUI(builder);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		List<UIInputComponent<?, ?>> inputs = builder.getInputs();

		createControls(parent, inputs);
	}

	@SuppressWarnings("unchecked")
	protected void createControls(Composite parent,
			List<UIInputComponent<?, ?>> inputs) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 2;
		layout.verticalSpacing = 9;

		for (final UIInputComponent<?, ?> input : inputs) {
			// Create the label
			Label label = new Label(container, SWT.NULL);
			label.setText(input.getLabel() == null ? input.getName() : input
					.getLabel());
			ControlBuilder controlBuilder = ControlBuilderRegistry.INSTANCE
					.getBuilderFor(input);
			controlBuilder.build(this, (UIInputComponent<?, Object>) input,
					container);
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
}
