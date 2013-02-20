/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.tools.forge.ui.wizards;

import java.lang.reflect.Field;
import java.util.List;

import org.eclipse.jface.wizard.Wizard;

public abstract class MutableWizard extends Wizard
{
   /**
    * Need to control pages manually, because eclipse doesn't allow deletion of pages
    */
   private List<ForgeWizardPage> pageList;

   @SuppressWarnings("unchecked")
   public MutableWizard()
   {
      super();
      try
      {
         Field field = Wizard.class.getDeclaredField("pages");
         field.setAccessible(true);
         pageList = (List<ForgeWizardPage>) field.get(this);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   /**
    * Return the pages of this Wizard
    *
    * @return
    */
   protected List<ForgeWizardPage> getPageList()
   {
      return pageList;
   }

}
