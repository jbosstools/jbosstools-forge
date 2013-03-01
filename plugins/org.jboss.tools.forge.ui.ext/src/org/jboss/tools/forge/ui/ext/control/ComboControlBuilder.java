/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.tools.forge.ui.ext.control;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.jboss.forge.convert.Converter;
import org.jboss.forge.convert.ConverterFactory;
import org.jboss.forge.ui.hints.InputType;
import org.jboss.forge.ui.hints.InputTypes;
import org.jboss.forge.ui.input.InputComponent;
import org.jboss.forge.ui.input.UISelectOne;
import org.jboss.forge.ui.util.InputComponents;
import org.jboss.tools.forge.ext.core.ForgeService;
import org.jboss.tools.forge.ui.ext.wizards.ForgeWizardPage;

public class ComboControlBuilder extends ControlBuilder {

    @Override
    @SuppressWarnings({ "unchecked" })
    public Control build(ForgeWizardPage page, final InputComponent<?, Object> input, final Composite container) {
        // Create the label
        Label label = new Label(container, SWT.NULL);
        label.setText(input.getLabel() == null ? input.getName() : input.getLabel());

        final Combo combo = new Combo(container, SWT.BORDER | SWT.SINGLE | SWT.READ_ONLY);

        final ConverterFactory converterFactory = ForgeService.INSTANCE.getConverterFactory();
        UISelectOne<Object> selectOne = (UISelectOne<Object>) input;
        Converter<Object, String> converter = (Converter<Object, String>) InputComponents.getItemLabelConverter(
            converterFactory, selectOne);
        String value = converter.convert(InputComponents.getValueFor(input));
        final Map<String, Object> items = new HashMap<String, Object>();
        Iterable<Object> valueChoices = selectOne.getValueChoices();
        if (valueChoices != null) {
            for (Object choice : valueChoices) {
                String itemLabel = converter.convert(choice);
                items.put(itemLabel, choice);
                combo.add(itemLabel);
            }
        }
        // Set Default Value
        combo.setText(value == null ? "" : value);

        combo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                int selectionIndex = combo.getSelectionIndex();
                if (selectionIndex != -1) {
                    String item = combo.getItem(selectionIndex);
                    InputComponents.setValueFor(converterFactory, input, items.get(item));
                }
            }
        });
        // Cleaning the map when the input is disposed
        combo.addDisposeListener(new DisposeListener() {
            @Override
            public void widgetDisposed(DisposeEvent e) {
                items.clear();
            }
        });
        return combo;
    }

    @Override
    protected Class<Object> getProducedType() {
        return Object.class;
    }

    @Override
    protected InputType getSupportedInputType() {
        return InputTypes.SELECT_ONE_DROPDOWN;
    }

    @Override
    protected Class<?>[] getSupportedInputComponentTypes() {
        return new Class<?>[] { UISelectOne.class };
    }
}
