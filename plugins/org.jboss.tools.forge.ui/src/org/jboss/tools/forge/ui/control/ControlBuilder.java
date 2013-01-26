/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.tools.forge.ui.control;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.jboss.forge.convert.Converter;
import org.jboss.forge.convert.ConverterFactory;
import org.jboss.forge.ui.UIInput;
import org.jboss.forge.ui.facets.HintsFacet;
import org.jboss.forge.ui.hints.InputType;
import org.jboss.tools.forge.core.ForgeService;
import org.jboss.tools.forge.ui.wizards.ForgeWizardPage;

/**
 * Builds a control
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
public abstract class ControlBuilder {

	/**
	 * Builds an Eclipse {@link Control} object based on the input
	 *
	 * @param page
	 *            TODO
	 * @param input
	 * @param converterRegistry
	 *            the converter registry to convert the inputed value from the
	 *            Control to the UIInput
	 *
	 * @return
	 */
	public abstract Control build(final ForgeWizardPage page,
			final UIInput<Object> input, final Composite container);

	/**
	 * Returns the supported type this control may produce
	 *
	 * @return
	 */
	protected abstract Class<?> getProducedType();

	/**
	 * Returns the supported input type for this component
	 *
	 * @return
	 */
	protected abstract InputType getSupportedInputType();

	public ConverterFactory getConverterFactory() {
		return ForgeService.INSTANCE.lookup(ConverterFactory.class);
	}

	/**
	 * Tests if this builder may handle this specific input
	 *
	 * @param input
	 * @return
	 */
	public boolean handles(UIInput<?> input) {
		boolean handles = false;
		InputType inputType = getInputType(input);
		if (inputType != null) {
			handles = inputType.equals(getSupportedInputType());
		} else {
			// Fallback to standard type
			handles = getProducedType().isAssignableFrom(input.getValueType());
		}
		return handles;
	}

	protected InputType getInputType(UIInput<?> input) {
		HintsFacet facet = input.getFacet(HintsFacet.class);
		return facet.getInputType();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void setInputValue(final UIInput<Object> input, Object value) {
		Object convertedType = value;
		if (value != null) {
			// TODO: Cache Converter ?
			ConverterFactory converterFactory = getConverterFactory();
			Class<? extends Object> source = value.getClass();
			Class<Object> target = input.getValueType();
			if (converterFactory != null) {
				Converter converter = converterFactory.getConverter(source,
						target);
				convertedType = converter.convert(value);
			} else {
				System.out
						.println("Converter Factory was not deployed !! Cannot convert from "
								+ source + " to " + target);
			}
		}
		input.setValue(convertedType);
	}

}