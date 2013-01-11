/*

 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.tools.forge.ui.wizards;

import java.util.Set;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.jboss.forge.container.AddonRegistry;
import org.jboss.forge.container.services.RemoteInstance;
import org.jboss.forge.ui.UICommand;
import org.jboss.tools.forge.core.ForgeService;

public class ForgeWizard extends Wizard implements INewWizard
{
   private UICommand uiCommand;

   public ForgeWizard()
   {
      setNeedsProgressMonitor(true);
   }

   @Override
   public void init(IWorkbench workbench, IStructuredSelection selection)
   {
      AddonRegistry addonRegistry = ForgeService.INSTANCE.getAddonRegistry();
      try
      {
         // TODO: Wait for Forge to init. This shouldn't be necessary
         Thread.sleep(3000);
      }
      catch (InterruptedException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      Set<RemoteInstance<UICommand>> remoteInstances = addonRegistry.getRemoteInstances(UICommand.class.getName());
      System.out.println("Remote Instances: " + remoteInstances);
      if (!remoteInstances.isEmpty())
      {
         this.uiCommand = remoteInstances.iterator().next().get();
      }
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

   }

   @Override
   public boolean performFinish()
   {
      return false;
   }

}
