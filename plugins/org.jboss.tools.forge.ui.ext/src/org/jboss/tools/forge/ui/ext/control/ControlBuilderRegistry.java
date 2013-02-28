/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.tools.forge.ui.ext.control;

import org.jboss.forge.ui.input.UIInputComponent;

/**
 * A factory for {@link ControlBuilder} instances.
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */
public enum ControlBuilderRegistry {
    INSTANCE;

    private ControlBuilder[] controlBuilders = { new CheckboxControlBuilder(), new ComboEnumControlBuilder(),
        new FileChooserControlBuilder(), new CheckboxTableControlBuilder(), new TextBoxControlBuilder(),
        new FallbackTextBoxControlBuilder() };

    public ControlBuilder getBuilderFor(UIInputComponent<?, ?> input) {
        for (ControlBuilder builder : controlBuilders) {
            if (builder.handles(input)) {
                return builder;
            }
        }
        throw new IllegalArgumentException("No UI component found for input type of " + input.getValueType());
    }
}
