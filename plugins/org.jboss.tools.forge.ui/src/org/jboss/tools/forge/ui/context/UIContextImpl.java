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
import org.jboss.forge.ui.UIContext;
import org.jboss.forge.ui.UIInput;
import org.jboss.forge.ui.UIValidationContext;
import org.jboss.forge.ui.wizard.UIWizardContext;

public class UIContextImpl implements UIWizardContext, UIValidationContext, UIContext, UIBuilder
{
   private List<UIInput<?>> inputs = new ArrayList<UIInput<?>>();
   private List<String> errors = new ArrayList<String>();

   public UIContextImpl()
   {
   }

   @Override
   public UIBuilder getUIBuilder()
   {
      return this;
   }

   @Override
   public UIBuilder add(UIInput<?> input)
   {
      inputs.add(input);
      return this;
   }

   public List<UIInput<?>> getInputs()
   {
      return inputs;
   }

   @Override
   public void addValidationError(UIInput<?> input, String errorMessage)
   {
      errors.add(errorMessage);
   }
}
