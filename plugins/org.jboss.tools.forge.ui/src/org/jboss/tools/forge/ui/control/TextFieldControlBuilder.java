/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.tools.forge.ui.control;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.jboss.forge.convert.ConverterFactory;
import org.jboss.forge.ui.UIInput;
import org.jboss.tools.forge.ui.wizards.ForgeWizardPage;

public class TextFieldControlBuilder extends ControlBuilder
{

   public TextFieldControlBuilder(ConverterFactory converterFactory)
   {
      super(converterFactory);
   }

   @Override
   public Control build(ForgeWizardPage page, final UIInput<Object> input, final Composite container)
   {
      final Text txt = new Text(container, SWT.BORDER | SWT.SINGLE);
      txt.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      txt.addModifyListener(new ModifyListener()
      {
         @Override
         public void modifyText(ModifyEvent e)
         {
            // Probably will need some sort of validation
            input.setValue(txt.getText());
         }
      });
      return txt;
   }

   @Override
   public boolean handles(UIInput<?> input)
   {
      return (input.getValueType() == String.class);
   }
}
