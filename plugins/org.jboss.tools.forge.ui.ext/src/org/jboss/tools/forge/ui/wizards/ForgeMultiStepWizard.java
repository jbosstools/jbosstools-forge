package org.jboss.tools.forge.ui.wizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.jboss.forge.ui.UICommand;
import org.jboss.forge.ui.result.NavigationResult;
import org.jboss.forge.ui.wizard.UIWizard;
import org.jboss.tools.forge.core.ForgeService;

public class ForgeMultiStepWizard extends ForgeWizard
{

   public ForgeMultiStepWizard(UIWizard stepBegin,
            IStructuredSelection selection)
   {
      super(stepBegin, selection);
      setForcePreviousAndNextButtons(true);
   }

   @Override
   public IWizardPage getNextPage(IWizardPage page)
   {
      UICommand uiCommand = ((ForgeWizardPage) page).getUICommand();
      if (!(uiCommand instanceof UIWizard))
      {
         return null;
      }
      UIWizard wiz = (UIWizard) uiCommand;
      NavigationResult nextCommand = null;
      try
      {
         nextCommand = wiz.next(getUiContext());
      }
      catch (Exception e)
      {
         // TODO: Use Eclipse logging mechanism
         e.printStackTrace();
      }
      // No next page
      if (nextCommand == null)
      {
         return null;
      }
      else
      {
         Class<? extends UICommand> successor = nextCommand.getNext();
         // Do we have any pages already displayed ? (Did we went back
         // already ?)
         ForgeWizardPage nextPage = (ForgeWizardPage) super
                  .getNextPage(page);
         if (nextPage == null)
         {
            UICommand nextStep = ForgeService.INSTANCE.lookup(successor);
            nextPage = new ForgeWizardPage(this, nextStep, getUiContext());
            addPage(nextPage);
         }
         return nextPage;
      }
   }

   @Override
   public boolean performFinish()
   {
      // TODO Auto-generated method stub
      return super.performFinish();
   }

}
