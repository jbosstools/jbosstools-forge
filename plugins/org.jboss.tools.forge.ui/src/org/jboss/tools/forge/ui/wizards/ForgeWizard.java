/*

 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.tools.forge.ui.wizards;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.jboss.forge.ui.UICommand;
import org.jboss.forge.ui.UIInput;
import org.jboss.tools.forge.ui.wizards.temp.GenderKind;
import org.jboss.tools.forge.ui.wizards.temp.UIInputImpl;

public class ForgeWizard extends Wizard implements INewWizard
{
   private UICommand uiCommand;
   private List<UIInput<?>> inputs = new ArrayList<UIInput<?>>();

   public ForgeWizard()
   {
      setNeedsProgressMonitor(true);

      // XXX: Mocking values until the classloader issue is fixed
      inputs.add(new UIInputImpl<String>("First Name", String.class));
      inputs.add(new UIInputImpl<String>("Last Name", String.class));
      inputs.add(new UIInputImpl<GenderKind>("Gender", GenderKind.class));
      inputs.add(new UIInputImpl<Boolean>("Accepts E-mail Notifications ?", Boolean.class));
   }

   @Override
   public void init(IWorkbench workbench, IStructuredSelection selection)
   {
      // AddonRegistry addonRegistry = ForgeService.INSTANCE.getAddonRegistry();
      // try
      // {
      // // TODO: Wait for Forge to init. This shouldn't be necessary
      // Thread.sleep(3000);
      // }
      // catch (InterruptedException e)
      // {
      // // TODO Auto-generated catch block
      // e.printStackTrace();
      // }
      // Set<RemoteInstance<UICommand>> remoteInstances = addonRegistry.getRemoteInstances(UICommand.class.getName());
      // System.out.println("Remote Instances: " + remoteInstances);
      // if (!remoteInstances.isEmpty())
      // {
      // this.uiCommand = remoteInstances.iterator().next().get();
      // }
   }

   @Override
   public void addPages()
   {
      if (this.uiCommand != null)
      {
         addPage(new ForgeWizardPage(this.uiCommand));
      }
      else
      {
         System.out.println("UI COMMAND IS NULL");
      }

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
