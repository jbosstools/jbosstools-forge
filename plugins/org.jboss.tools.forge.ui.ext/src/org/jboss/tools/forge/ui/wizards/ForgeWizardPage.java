/*
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.tools.forge.ui.wizards;

import java.net.URL;
import java.util.List;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.jboss.forge.ui.UICommand;
import org.jboss.forge.ui.UICommandMetadata;
import org.jboss.forge.ui.input.UIInputComponent;
import org.jboss.tools.forge.ui.ForgeUIPlugin;
import org.jboss.tools.forge.ui.context.UIBuilderImpl;
import org.jboss.tools.forge.ui.context.UIContextImpl;
import org.jboss.tools.forge.ui.context.UIValidationContextImpl;
import org.jboss.tools.forge.ui.control.ControlBuilder;
import org.jboss.tools.forge.ui.control.ControlBuilderRegistry;

/**
 * A Forge Wizard Page
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
public class ForgeWizardPage extends WizardPage {
    private UICommand uiCommand;
    private UIContextImpl uiContext;

    public ForgeWizardPage(Wizard wizard, UICommand command, UIContextImpl contextImpl) {
        super("Page Name");
        setWizard(wizard);
        UICommandMetadata id = command.getMetadata();
        setTitle(id.getName());
        setDescription(id.getDescription());
        setImageDescriptor(ForgeUIPlugin.getForgeLogo());
        this.uiCommand = command;
        this.uiContext = contextImpl;
    }

    public UICommand getUICommand() {
        return uiCommand;
    }

    @Override
    public void createControl(Composite parent) {
        UIBuilderImpl builder = new UIBuilderImpl(uiContext);
        try {
            uiCommand.initializeUI(builder);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        List<UIInputComponent<?, ?>> inputs = builder.getInputs();

        createControls(parent, inputs);
    }

    @SuppressWarnings("unchecked")
    protected void createControls(Composite parent, List<UIInputComponent<?, ?>> inputs) {
        Composite container = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        container.setLayout(layout);
        layout.numColumns = 2;
        layout.verticalSpacing = 9;

        for (final UIInputComponent<?, ?> input : inputs) {
            // Create the label
            Label label = new Label(container, SWT.NULL);
            label.setText(input.getLabel() == null ? input.getName() : input.getLabel());
            ControlBuilder controlBuilder = ControlBuilderRegistry.INSTANCE.getBuilderFor(input);
            Control control = controlBuilder.build(this, (UIInputComponent<?, Object>) input, container);

            // Update page status
            Listener pageCompleteListener = new Listener() {
                @Override
                public void handleEvent(Event event) {
                    if (isCurrentPage()) {
                        isPageComplete();
                        // Refresh the buttons
                        getContainer().updateButtons();
                    }
                    event.doit = true;
                }
            };
            control.addListener(SWT.Modify, pageCompleteListener);
            control.addListener(SWT.DefaultSelection, pageCompleteListener);
            control.addListener(SWT.Selection, pageCompleteListener);
        }
        setControl(container);
    }

    /**
     * Validates the method
     */
    @Override
    public boolean isPageComplete() {
        // clear error message
        setErrorMessage(null);
        UIValidationContextImpl validationContext = new UIValidationContextImpl(uiContext);
        // invokes the validation in the current UICommand
        uiCommand.validate(validationContext);
        List<String> errors = validationContext.getErrors();
        boolean noErrors = errors.isEmpty();
        if (!noErrors) {
            setErrorMessage(errors.get(0));
        }
        // if no errors were found, the page is ready to go to the next step
        return noErrors;
    }

    @Override
    public void performHelp() {
        UICommandMetadata metadata = uiCommand.getMetadata();
        URL docLocation = metadata.getDocLocation();
        if (docLocation != null) {
//            PlatformUI.getWorkbench().getHelpSystem().displayHelp(docLocation.toExternalForm());
        }
    }
}
