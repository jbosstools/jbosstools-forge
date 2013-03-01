/*
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.tools.forge.ui.ext.wizards;

import java.net.URL;
import java.util.List;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.jboss.forge.ui.UICommand;
import org.jboss.forge.ui.input.InputComponent;
import org.jboss.forge.ui.metadata.UICommandMetadata;
import org.jboss.forge.ui.util.InputComponents;
import org.jboss.tools.forge.ui.ext.ForgeUIPlugin;
import org.jboss.tools.forge.ui.ext.context.UIBuilderImpl;
import org.jboss.tools.forge.ui.ext.context.UIContextImpl;
import org.jboss.tools.forge.ui.ext.context.UIValidationContextImpl;
import org.jboss.tools.forge.ui.ext.control.ControlBuilder;
import org.jboss.tools.forge.ui.ext.control.ControlBuilderRegistry;

/**
 * A Forge Wizard Page
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
public class ForgeWizardPage extends WizardPage implements Listener {
    private UICommand uiCommand;
    private UIContextImpl uiContext;
    private UIBuilderImpl uiBuilder;

    public ForgeWizardPage(Wizard wizard, UICommand command, UIContextImpl contextImpl) {
        super("Page Name");
        setWizard(wizard);
        setPageComplete(false);
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
        uiBuilder = new UIBuilderImpl(uiContext);
        try {
            uiCommand.initializeUI(uiBuilder);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        List<InputComponent<?, ?>> inputs = uiBuilder.getInputs();
        createControls(parent, inputs);
    }

    @SuppressWarnings("unchecked")
    protected void createControls(Composite parent, List<InputComponent<?, ?>> inputs) {
        Composite container = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        container.setLayout(layout);
        layout.numColumns = 2;
        layout.verticalSpacing = 9;

        for (final InputComponent<?, ?> input : inputs) {
            ControlBuilder controlBuilder = ControlBuilderRegistry.INSTANCE.getBuilderFor(input);
            Control control = controlBuilder.build(this, (InputComponent<?, Object>) input, container);

            // Update page status
            control.addListener(SWT.Modify, this);
            control.addListener(SWT.DefaultSelection, this);
            control.addListener(SWT.Selection, this);
        }
        setPageComplete(validatePage());
        // Show description on opening
        setErrorMessage(null);
        setMessage(null);
        setControl(container);
    }

    /**
     * The <code>ForgeWizardPage</code> implementation of this <code>Listener</code> method handles all events and
     * enablements for controls on this page.
     */
    @Override
    public void handleEvent(Event event) {
        if (isCurrentPage()) {
            setPageComplete(validatePage());
            // Refresh the buttons
            getContainer().updateButtons();
        }
        event.doit = true;
    }

    /**
     * Returns whether this page's controls currently all contain valid values. It also calls
     * {@link ForgeWizardPage#setErrorMessage(String)} if an error is found
     *
     * @return <code>true</code> if all controls are valid, and <code>false</code> if at least one is invalid
     */
    public boolean validatePage() {
        // clear error message
        setErrorMessage(null);

        // Validate required
        if (uiBuilder != null) {
            for (InputComponent<?, ?> input : uiBuilder.getInputs()) {
                String requiredMsg = InputComponents.validateRequired(input);
                if (requiredMsg != null) {
                    setErrorMessage(requiredMsg);
                    return false;
                }
            }
        }

        // Invoke custom validation
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
            // PlatformUI.getWorkbench().getHelpSystem().displayHelp(docLocation.toExternalForm());
        }
    }
}
