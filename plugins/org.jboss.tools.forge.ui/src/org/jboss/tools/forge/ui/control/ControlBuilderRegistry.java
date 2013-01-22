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
import org.jboss.forge.convert.ConverterRegistry;
import org.jboss.forge.ui.UIInput;

/**
 * A factory for {@link ControlBuilder} instances.
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
public class ControlBuilderRegistry
{
   private List<ControlBuilder> controlBuilders = new ArrayList<ControlBuilder>();

   public ControlBuilderRegistry(ConverterRegistry converterRegistry)
   {
      registerBuilders(converterRegistry);
   }

   private void registerBuilders(ConverterRegistry converterRegistry)
   {
      controlBuilders.add(new TextFieldControlBuilder(converterRegistry));
      controlBuilders.add(new CheckboxControlBuilder(converterRegistry));
      controlBuilders.add(new ComboListControlBuilder(converterRegistry));

      // This must always be the last one in list
      controlBuilders.add(new FallbackTextFieldControlBuilder(converterRegistry));
   }

   @SuppressWarnings("unchecked")
   public Control build(UIInput<?> input, Composite parent)
   {
      for (ControlBuilder builder : controlBuilders)
      {
         if (builder.handles(input))
         {
            return builder.build(((UIInput<Object>) input), parent);
         }
      }
      throw new IllegalArgumentException("No UI component found for input type of " + input.getValueType());
   }
}
