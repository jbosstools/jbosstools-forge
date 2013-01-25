/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.tools.forge.ui.control;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.jboss.forge.ui.UIInput;
import org.jboss.tools.forge.ui.wizards.ForgeWizardPage;

/**
 * A factory for {@link ControlBuilder} instances.
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
public enum ControlBuilderRegistry
{
   INSTANCE;

   private List<ControlBuilder> controlBuilders = new ArrayList<ControlBuilder>();

   private ControlBuilderRegistry()
   {
      controlBuilders.add(new TextFieldControlBuilder());
      controlBuilders.add(new CheckboxControlBuilder());
      controlBuilders.add(new ComboListControlBuilder());
      controlBuilders.add(new FileChooserControlBuilder());

      // This must always be the last one in list
      controlBuilders.add(new FallbackTextFieldControlBuilder());
   }

   @SuppressWarnings("unchecked")
   public Control build(ForgeWizardPage page, UIInput<?> input, Composite parent)
   {
      for (ControlBuilder builder : controlBuilders)
      {
         if (builder.handles(input))
         {
            return builder.build(page, ((UIInput<Object>) input), parent);
         }
      }
      throw new IllegalArgumentException("No UI component found for input type of " + input.getValueType());
   }
}
