/*

 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.tools.forge.ui.wizards;

import java.util.Set;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.jboss.forge.container.AddonRegistry;
import org.jboss.forge.container.services.ExportedInstance;
import org.jboss.forge.ui.Result;
import org.jboss.forge.ui.ResultFail;
import org.jboss.forge.ui.ResultSuccess;
import org.jboss.forge.ui.UICommand;
import org.jboss.forge.ui.wizard.UIWizard;
import org.jboss.forge.ui.wizard.UIWizardEnd;
import org.jboss.tools.forge.core.ForgeService;
import org.jboss.tools.forge.ui.context.UIContextImpl;

public class ForgeWizard extends Wizard implements INewWizard
{
   private UICommand uiCommand;

   private UIContextImpl uiContext = new UIContextImpl();

   private IStructuredSelection selection;

   public ForgeWizard()
   {
      setNeedsProgressMonitor(true);
   }

   protected void initForge()
   {
      ForgeService.INSTANCE.getAddonRegistry();
      try
      {
         // TODO: Wait for Forge to init. This shouldn't be necessary
         Thread.sleep(3000);
      }
      catch (InterruptedException e)
      {
         e.printStackTrace();
      }
   }

   @Override
   public void init(IWorkbench workbench, IStructuredSelection selection)
   {
      initForge();
      this.selection = selection;
      lookupServices();
   }

   private void lookupServices()
   {
      AddonRegistry addonRegistry = ForgeService.INSTANCE.getAddonRegistry();
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
         ForgeWizardPage page = new ForgeWizardPage(this, uiCommand, uiContext);
         addPage(page);
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
      System.out.println("Inputs :" + uiContext.getInputs());
      try
      {
         Result result = uiCommand.execute(uiContext);
         if (result instanceof ResultSuccess)
         {
            MessageDialog.openError(getShell(), "Success !",
                     result.getMessage() == null ? "Command successfully executed !" : result.getMessage());
         }
         else if (result instanceof ResultFail)
         {
            MessageDialog.openError(getShell(), "Error", ((ResultFail) result).getMessage());
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
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

   public IStructuredSelection getSelection()
   {
      return selection;
   }
}
