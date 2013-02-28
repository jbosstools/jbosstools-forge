/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.tools.forge.ui.ext.control;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.jboss.forge.convert.Converter;
import org.jboss.forge.convert.ConverterFactory;
import org.jboss.forge.ui.hints.InputType;
import org.jboss.forge.ui.hints.InputTypes;
import org.jboss.forge.ui.input.InputComponent;
import org.jboss.forge.ui.input.UIInput;
import org.jboss.tools.forge.ui.ext.Inputs;
import org.jboss.tools.forge.ui.ext.wizards.ForgeWizardPage;

public class CheckboxControlBuilder extends ControlBuilder {

    @Override
    public Control build(ForgeWizardPage page, final InputComponent<?, Object> input, final Composite container) {
        // Create the label
        Label label = new Label(container, SWT.NULL);
        label.setText(input.getLabel() == null ? input.getName() : input.getLabel());

        Button cmb = new Button(container, SWT.CHECK);
        cmb.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        // Set Default Value
        ConverterFactory converterFactory = Inputs.getConverterFactory();
        if (converterFactory != null) {
            Converter<Object, Boolean> converter = converterFactory.getConverter(input.getValueType(), Boolean.class);
            Boolean value = converter.convert(Inputs.getValueFor(input));
            cmb.setSelection(value == null ? false : value);
        }

        // cmd.setSelection(value == null ? false : value);
        cmb.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                boolean selection = ((Button) e.widget).getSelection();
                Inputs.setValueFor(input, selection);
            }
        });
        return cmb;
    }

    @Override
    protected Class<Boolean> getProducedType() {
        return Boolean.class;
    }

    @Override
    protected InputType getSupportedInputType() {
        return InputTypes.CHECKBOX;
    }

    @Override
    protected Class<?>[] getSupportedInputComponentTypes() {
        return new Class<?>[] { UIInput.class };
    }
}
