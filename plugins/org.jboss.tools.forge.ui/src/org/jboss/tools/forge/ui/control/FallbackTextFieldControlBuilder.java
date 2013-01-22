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
import org.jboss.forge.convert.Converter;
import org.jboss.forge.convert.ConverterRegistry;
import org.jboss.forge.ui.UIInput;

/**
 * Renders a textfield and converts the value to the expected input type
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
public class FallbackTextFieldControlBuilder extends ControlBuilder
{

   public FallbackTextFieldControlBuilder(ConverterRegistry converterRegistry)
   {
      super(converterRegistry);
   }

   @Override
   public Control build(final UIInput<Object> input, final Composite container)
   {
      final Text txt = new Text(container, SWT.BORDER | SWT.SINGLE);
      txt.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

      txt.addModifyListener(new ModifyListener()
      {
         @Override
         public void modifyText(ModifyEvent e)
         {
            Converter<String, ?> converter = getConverterRegistry().getConverter(String.class, input.getValueType());
            Object convertedValue = converter.convert(txt.getText());
            input.setValue(convertedValue);
         }
      });
      return txt;
   }

   @Override
   public boolean handles(UIInput<?> input)
   {
      return true;
   }

}
