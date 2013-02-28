/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.tools.forge.ui.ext.context;

import java.util.ArrayList;
import java.util.List;

import org.jboss.forge.ui.context.UIContext;
import org.jboss.forge.ui.context.UIValidationContext;
import org.jboss.forge.ui.input.UIInputComponent;

public class UIValidationContextImpl implements UIValidationContext {
    private List<String> errors = new ArrayList<String>();
    private UIContext context;

    public UIValidationContextImpl(UIContext context) {
        this.context = context;
    }

    @Override
    public void addValidationError(UIInputComponent<?, ?> input, String errorMessage) {
        errors.add(errorMessage);
    }

    public List<String> getErrors() {
        return errors;
    }

    @Override
    public UIContext getUIContext() {
        return context;
    }
}
