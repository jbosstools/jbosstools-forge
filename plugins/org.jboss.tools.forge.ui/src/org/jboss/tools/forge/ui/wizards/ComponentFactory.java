/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.tools.forge.ui.wizards;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.jboss.forge.ui.UIInput;

/**
 * Creates a control based on the value type
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
public class ComponentFactory
{
   @SuppressWarnings({ "unchecked", "rawtypes" })
   public static Control createComponent(final UIInput<?> input, final Composite container)
   {
      Control control;
      if (input.getValueType() == String.class)
      {
         Text txt = new Text(container, SWT.BORDER | SWT.SINGLE);
         txt.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
         txt.addModifyListener(new ModifyListener()
         {
            @Override
            public void modifyText(ModifyEvent e)
            {
               ((UIInput<String>) input).setValue(((Text) e.widget).getText());
            }
         });
         control = txt;
      }
      else if (input.getValueType() == Boolean.class)
      {
         Button cmb = new Button(container, SWT.CHECK);
         cmb.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
         cmb.addSelectionListener(new SelectionAdapter()
         {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
               ((UIInput<Boolean>) input).setValue(((Button) e.widget).getSelection());
            }
         });
         control = cmb;
      }
      else if (Enum.class.isAssignableFrom(input.getValueType()))
      {
         final Combo combo = new Combo(container, SWT.BORDER | SWT.SINGLE | SWT.READ_ONLY);
         Enum[] enumConstants = input.getValueType().asSubclass(Enum.class).getEnumConstants();
         for (Enum enum1 : enumConstants)
         {
            combo.add(enum1.name());
         }
         combo.addSelectionListener(new SelectionAdapter()
         {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
               int selectionIndex = combo.getSelectionIndex();
               if (selectionIndex != -1)
               {
                  String item = combo.getItem(selectionIndex);
                  Enum enumItem = Enum.valueOf((Class<Enum>) input.getValueType(), item);
                  ((UIInput<Enum>) input).setValue(enumItem);
               }
            }
         });
         control = combo;
      }
      else
      {
         control = null;
      }
      return control;
   }
}
