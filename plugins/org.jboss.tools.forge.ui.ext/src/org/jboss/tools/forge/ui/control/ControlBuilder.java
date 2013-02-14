/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.tools.forge.ui.control;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.jboss.forge.convert.Converter;
import org.jboss.forge.convert.ConverterFactory;
import org.jboss.forge.ui.UIInput;
import org.jboss.forge.ui.UIInputComponent;
import org.jboss.forge.ui.UISelectMany;
import org.jboss.forge.ui.UISelectOne;
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
@SuppressWarnings({ "rawtypes", "unchecked" })
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
			final UIInputComponent<?, Object> input, final Composite container);

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

	/**
	 * Tests if this builder may handle this specific input
	 * 
	 * @param input
	 * @return
	 */
	public boolean handles(UIInputComponent<?, ?> input) {
		boolean handles = false;
		InputType inputTypeHint = getInputType(input);

		Iterable<Class<?>> supportedTypes = getSupportedInputComponentTypes();
		for (Class<?> inputType : supportedTypes) {
			if (inputType.isAssignableFrom(input.getClass())) {
				handles = true;
				break;
			}
		}

		if (handles) {
			if (inputTypeHint != null) {
				// FIXME: Equals method not working on proxied types
				handles = inputTypeHint.toString().equals(
						getSupportedInputType().toString());
			} else {
				// Fallback to standard type
				handles = getProducedType().isAssignableFrom(
						input.getValueType());
			}
		}

		return handles;
	}

	protected abstract Iterable<Class<?>> getSupportedInputComponentTypes();

	protected InputType getInputType(UIInputComponent<?, ?> input) {
		InputType result = null;
		if (input.hasFacet(HintsFacet.class)) {
			HintsFacet facet = input.getFacet(HintsFacet.class);
			result = facet.getInputType();
		}
		return result;
	}

	protected Object getValueFor(UIInputComponent<?, ?> component) {
		if (component instanceof UIInput) {
			return ((UIInput<Object>) component).getValue();
		} else if (component instanceof UISelectOne) {
			return ((UISelectOne<Object>) component).getValue();
		} else if (component instanceof UISelectMany) {
			return ((UISelectMany<Object>) component).getValue();
		} else {
			return null;
		}
	}

	protected void setValueFor(UIInputComponent<?, ?> component, Object value) {
		if (component instanceof UIInput) {
			setInputValue((UIInput<Object>) component, value);
		} else if (component instanceof UISelectOne) {
			setInputValue((UISelectOne) component, value);
		} else if (component instanceof UISelectMany) {
			setInputValue((UISelectMany) component, value);
		}
	}

	private void setInputValue(final UIInput<Object> input, Object value) {
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
				System.err
						.println("Converter Factory was not deployed !! Cannot convert from "
								+ source + " to " + target);
			}
		}
		input.setValue(convertedType);
	}

	private void setInputValue(final UISelectOne<Object> input, Object value) {
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
				System.err
						.println("Converter Factory was not deployed !! Cannot convert from "
								+ source + " to " + target);
			}
		}
		input.setValue(convertedType);
	}

	private void setInputValue(final UISelectMany<Object> input, Object value) {
		if (value != null) {
			// TODO: Cache Converter ?

			if (value instanceof Iterable) {
				Iterable<Object> values = (Iterable) value;

				List<Object> convertedValues = new ArrayList<Object>();
				for (Object object : values) {
					convertedValues.add(convertValue(input.getValueType(),
							object));
				}
				input.setValue(values);
			} else {
				input.setValue(Arrays.asList(convertValue(input.getValueType(),
						value)));
			}

		}
	}

	public <T> T convertValue(Class<T> targetType, Object value) {
		ConverterFactory converterFactory = getConverterFactory();
		Class<? extends Object> source = value.getClass();
		if (converterFactory != null) {
			Converter converter = converterFactory.getConverter(source,
					targetType);
			return (T) converter.convert(value);
		} else {
			System.err
					.println("Converter Factory was not deployed !! Cannot convert from "
							+ source + " to " + targetType);
			return (T) value;
		}
	}

	/**
	 * Utility method
	 * 
	 * @return the converter factory or null if not found
	 */
	protected ConverterFactory getConverterFactory() {
		return ForgeService.INSTANCE.lookup(ConverterFactory.class);
	}

}