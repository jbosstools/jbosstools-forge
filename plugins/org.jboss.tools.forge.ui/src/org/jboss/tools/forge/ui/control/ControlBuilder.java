/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.tools.forge.ui.control;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.jboss.forge.convert.ConverterFactory;
import org.jboss.forge.ui.UIInput;

/**
 * Builds a control
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
public abstract class ControlBuilder
{

   private final ConverterFactory converterFactory;

   protected ControlBuilder(ConverterFactory converterRegistry)
   {
      this.converterFactory = converterRegistry;
   }

   /**
    * Builds an Eclipse {@link Control} object based on the input
    *
    * @param input
    *
    * @param converterRegistry the converter registry to convert the inputed value from the Control to the UIInput
    * @return
    */
   public abstract Control build(final UIInput<Object> input, final Composite container);

   /**
    * Tests if this builder may handle this specific input
    *
    * @param input
    * @return
    */
   public abstract boolean handles(final UIInput<?> input);

   protected ConverterFactory getConverterFactory()
   {
      return converterFactory;
   }
}