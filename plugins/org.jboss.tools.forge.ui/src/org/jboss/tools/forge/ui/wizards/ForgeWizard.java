/*

 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.tools.forge.ui.wizards;

import java.util.Set;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.jboss.forge.container.AddonRegistry;
import org.jboss.forge.container.services.ExportedInstance;
import org.jboss.forge.convert.ConverterRegistry;
import org.jboss.forge.ui.UICommand;
import org.jboss.forge.ui.wizard.UIWizard;
import org.jboss.forge.ui.wizard.UIWizardEnd;
import org.jboss.tools.forge.core.ForgeService;
import org.jboss.tools.forge.ui.control.ControlBuilderRegistry;

public class ForgeWizard extends Wizard implements INewWizard
{
   private UICommand uiCommand;
   // private UICommand current;
   private ControlBuilderRegistry controlBuilderRegistry;

   public ForgeWizard()
   {
      setNeedsProgressMonitor(true);
   }

   @Override
   public void init(IWorkbench workbench, IStructuredSelection selection)
   {
      lookupServices();
   }

   private void lookupServices()
   {
      AddonRegistry addonRegistry = ForgeService.INSTANCE.getAddonRegistry();
      try
      {
         // TODO: Wait for Forge to init. This shouldn't be necessary
         Thread.sleep(3000);
      }
      catch (InterruptedException e)
      {
         e.printStackTrace();
      }
      ExportedInstance<ConverterRegistry> convertInstance = addonRegistry
               .getExportedInstance(ConverterRegistry.class);

      if (convertInstance != null)
      {
         ConverterRegistry converterRegistry = convertInstance.get();
         controlBuilderRegistry = new ControlBuilderRegistry(converterRegistry);
      }
      Set<ExportedInstance<UICommand>> exportedInstances = addonRegistry.getExportedInstances(UICommand.class);
      System.out.println("Available UICommands: " + exportedInstances);
      if (!exportedInstances.isEmpty())
      {
         this.uiCommand = exportedInstances.iterator().next().get();
      }
   }

   @Override
   public void addPages()
   {
      if (this.uiCommand != null)
      {
         addPage(new ForgeWizardPage(uiCommand, controlBuilderRegistry));
      }
      else
      {
         System.out.println("UI COMMAND IS NULL");
      }
   }

   @Override
   public IWizardPage getStartingPage()
   {
      // TODO Auto-generated method stub
      return super.getStartingPage();
   }

   @Override
   public IWizardPage getNextPage(IWizardPage page)
   {
      // TODO Auto-generated method stub
      return super.getNextPage(page);
   }

   @Override
   public IWizardPage getPreviousPage(IWizardPage page)
   {
      // TODO Auto-generated method stub
      return super.getPreviousPage(page);
   }

   @Override
   public boolean performFinish()
   {
      return true;
   }

   @Override
   public boolean needsPreviousAndNextButtons()
   {
      return isWizardCommand();
   }

   private boolean isWizardCommand()
   {
      return uiCommand instanceof UIWizard;
   }

   @Override
   public boolean canFinish()
   {
      if (isWizardCommand())
      {
         return (uiCommand instanceof UIWizardEnd);
      }
      return true;
   }
}
