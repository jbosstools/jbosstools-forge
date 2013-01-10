/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.tools.forge.ui.wizards;

import java.util.List;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.jboss.forge.ui.UICommand;
import org.jboss.forge.ui.UIInput;
import org.jboss.tools.forge.ui.context.UIContextImpl;

/**
 * A Forge Wizard Page
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
public class ForgeWizardPage extends WizardPage
{
   private UICommand ui;


   public ForgeWizardPage(UICommand command)
   {
      super("Page Title");
      this.ui = command;
   }

   @Override
   public void createControl(Composite parent)
   {
      UIContextImpl ctx = new UIContextImpl();
      try
      {
         ui.initializeUI(ctx);
      }
      catch (Exception e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }

      List<UIInput<?>> inputs = ctx.getInputs();

      Composite container = new Composite(parent, SWT.NULL);
      GridLayout layout = new GridLayout();
      container.setLayout(layout);
      layout.numColumns = 3;
      layout.verticalSpacing = 9;

      for (final UIInput<?> uiInput : inputs)
      {
         // Create the label
         Label label = new Label(container, SWT.NULL);
         label.setText(uiInput.getLabel());

         // TODO: Each type should render a specific component (String = Text, Boolean = Checkbox, etc)
         Text txt = new Text(container, SWT.BORDER | SWT.SINGLE);
         txt.addModifyListener(new ModifyListener()
         {
            @SuppressWarnings("unchecked")
            @Override
            public void modifyText(ModifyEvent e)
            {
               ((UIInput<String>) uiInput).setValue(((Text) e.widget).getText());
            }
         });
      }
      setControl(container);
   }

   public void updateStatus(String message)
   {
      setErrorMessage(message);
      setPageComplete(message == null);
   }

}
