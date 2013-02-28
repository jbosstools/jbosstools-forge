/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.tools.forge.ui.ext.control;

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
import org.jboss.forge.convert.Converter;
import org.jboss.forge.convert.ConverterFactory;
import org.jboss.forge.ui.hints.InputType;
import org.jboss.forge.ui.hints.InputTypes;
import org.jboss.forge.ui.input.UIInput;
import org.jboss.forge.ui.input.UIInputComponent;
import org.jboss.tools.forge.ui.ext.Inputs;
import org.jboss.tools.forge.ui.ext.wizards.ForgeWizardPage;

public class JavaClassChooserControlBuilder extends ControlBuilder {

    @Override
    public Control build(final ForgeWizardPage page, final UIInputComponent<?, Object> input, final Composite parent) {
        // Create the label
        Label label = new Label(parent, SWT.NULL);
        label.setText(input.getLabel() == null ? input.getName() : input.getLabel());

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
        ConverterFactory converterFactory = Inputs.getConverterFactory();
        if (converterFactory != null) {
            Converter<Object, String> converter = converterFactory.getConverter(input.getValueType(), String.class);
            String value = converter.convert(Inputs.getValueFor(input));
            containerText.setText(value == null ? "" : value);
        }

        containerText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                String text = containerText.getText();
                if (text != null) {
                    Inputs.setValueFor(input, text);
                }
            }
        });

        Button button = new Button(container, SWT.PUSH);
        button.setText("Browse...");
        button.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {

                // TODO: Open Java Class chooser
            }
        });
        return containerText;
    }

    @Override
    protected Class<String> getProducedType() {
        return String.class;
    }

    @Override
    protected InputType getSupportedInputType() {
        return InputTypes.JAVA_CLASS_PICKER;
    }

    @Override
    protected Class<?>[] getSupportedInputComponentTypes() {
        return new Class<?>[] { UIInput.class };
    }
}
