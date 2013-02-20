/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.tools.forge.ui.context;

import java.util.ArrayList;
import java.util.List;

import org.jboss.forge.ui.UIBuilder;
import org.jboss.forge.ui.context.UIContext;
import org.jboss.forge.ui.input.UIInputComponent;

public class UIBuilderImpl implements UIBuilder {
    private List<UIInputComponent<?, ?>> inputs = new ArrayList<UIInputComponent<?, ?>>();
    private UIContext context;

    public UIBuilderImpl(UIContext context) {
        this.context = context;
    }

    @Override
    public UIBuilder add(UIInputComponent<?, ?> input) {
        inputs.add(input);
        return this;
    }

    public List<UIInputComponent<?, ?>> getInputs() {
        return inputs;
    }

    @Override
    public UIContext getUIContext() {
        return context;
    }
}
