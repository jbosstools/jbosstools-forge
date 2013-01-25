/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.tools.forge.ui.control;

import java.util.Arrays;
import java.util.List;

import org.jboss.forge.ui.UIInput;

/**
 * A factory for {@link ControlBuilder} instances.
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
public enum ControlBuilderRegistry
{
   INSTANCE;

   private List<ControlBuilder> controlBuilders = Arrays.asList(
            new TextBoxControlBuilder(),
            new CheckboxControlBuilder(),
            new EnumComboControlBuilder(),
            new FileChooserControlBuilder(),
            new FallbackTextBoxControlBuilder());

   public ControlBuilder getBuilderFor(UIInput<?> input)
   {
      for (ControlBuilder builder : controlBuilders)
      {
         if (builder.handles(input))
         {
            return builder;
         }
      }
      throw new IllegalArgumentException("No UI component found for input type of " + input.getValueType());
   }
}
