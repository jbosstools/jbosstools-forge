/*

 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.tools.forge.ui.wizards.mock;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.jboss.forge.ui.UIInput;
import org.jboss.tools.forge.ui.wizards.ForgeWizard;

public class ForgeMockWizard extends ForgeWizard implements INewWizard
{
   private List<UIInput<?>> inputs = new ArrayList<UIInput<?>>();

   public ForgeMockWizard()
   {
      setNeedsProgressMonitor(true);
   }

   @Override
   public void init(IWorkbench workbench, IStructuredSelection selection)
   {
      initForge();
      inputs.add(new UIInputImpl<String>("First Name", String.class));
      inputs.add(new UIInputImpl<String>("Last Name", String.class));
      inputs.add(new UIInputImpl<GenderKind>("Gender", GenderKind.class));
      inputs.add(new UIInputImpl<Boolean>("Accepts E-mail Notifications ?", Boolean.class));
   }

   @Override
   public void addPages()
   {
      addPage(new ForgeMockWizardPage(inputs));
   }

   @Override
   public boolean performFinish()
   {
      for (UIInput<?> input : inputs)
      {
         System.out.println("Input " + input.getName() + " - " + input.getValue());
      }
      return false;
   }

   @Override
   public boolean needsPreviousAndNextButtons()
   {
      return false;
   }
}
