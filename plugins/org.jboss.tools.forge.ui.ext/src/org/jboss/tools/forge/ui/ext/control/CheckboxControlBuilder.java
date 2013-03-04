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
import org.jboss.forge.ui.util.InputComponents;
import org.jboss.tools.forge.ext.core.ForgeService;
import org.jboss.tools.forge.ui.ext.wizards.ForgeWizardPage;

public class CheckboxControlBuilder extends ControlBuilder {

    @Override
    public Control build(ForgeWizardPage page, final InputComponent<?, Object> input, final Composite container) {
        // Create the label
        new Label(container, SWT.NULL);
        Button cmb = new Button(container, SWT.CHECK);
        cmb.setText(input.getLabel() == null ? input.getName() : input.getLabel());
        cmb.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        // Set Default Value
        final ConverterFactory converterFactory = ForgeService.INSTANCE.getConverterFactory();
        if (converterFactory != null) {
            Converter<Object, Boolean> converter = converterFactory.getConverter(input.getValueType(), Boolean.class);
            Boolean value = converter.convert(InputComponents.getValueFor(input));
            cmb.setSelection(value == null ? false : value);
        }

        // cmd.setSelection(value == null ? false : value);
        cmb.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                boolean selection = ((Button) e.widget).getSelection();
                InputComponents.setValueFor(converterFactory, input, selection);
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
