/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.tools.forge.ui.wizards.mock;

import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.jboss.forge.ui.UIInput;
import org.jboss.tools.forge.ui.wizards.ForgeWizardPage;

public class ForgeMockWizardPage extends ForgeWizardPage
{

   private final List<UIInput<?>> inputs;

   public ForgeMockWizardPage(List<UIInput<?>> inputs)
   {
      super(null);
      this.inputs = inputs;
   }

   public List<UIInput<?>> getInputs()
   {
      return inputs;
   }

   @Override
   public void createControl(Composite parent)
   {
      createControls(parent, inputs);
   }
}