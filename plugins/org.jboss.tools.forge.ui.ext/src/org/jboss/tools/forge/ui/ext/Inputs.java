/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.tools.forge.ui.ext;

import java.util.ArrayList;
import java.util.List;

import org.jboss.forge.convert.CompositeConverter;
import org.jboss.forge.convert.Converter;
import org.jboss.forge.convert.ConverterFactory;
import org.jboss.forge.ui.facets.HintsFacet;
import org.jboss.forge.ui.hints.InputType;
import org.jboss.forge.ui.input.InputComponent;
import org.jboss.forge.ui.input.ManyValued;
import org.jboss.forge.ui.input.SelectComponent;
import org.jboss.forge.ui.input.SingleValued;
import org.jboss.tools.forge.ext.core.ForgeService;

/**
 * Utilities for {@link InputComponent} objects
 *
 * TODO: May be moved to ui-api in the future
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public abstract class Inputs {

    /**
     * @return the {@link InputType} object associated to this {@link InputComponent}
     */
    public static InputType getInputType(InputComponent<?, ?> input) {
        InputType result = null;
        if (input.hasFacet(HintsFacet.class)) {
            HintsFacet facet = input.getFacet(HintsFacet.class);
            result = facet.getInputType();
        }
        return result;
    }

    /**
     * @return the value stored in this {@link InputComponent}
     */
    public static Object getValueFor(InputComponent<?, ?> component) {
        if (component instanceof SingleValued) {
            return ((SingleValued<?, Object>) component).getValue();
        } else if (component instanceof ManyValued) {
            return ((ManyValued<?, Object>) component).getValue();
        } else {
            return null;
        }
    }

    /**
     * Sets the value in the provided {@link InputComponent}, making any necessary conversions
     *
     * @param component
     * @param value
     */
    public static void setValueFor(InputComponent<?, Object> component, Object value) {
        if (component instanceof SingleValued) {
            setSingleInputValue(component, value);
        } else if (component instanceof ManyValued) {
            setManyInputValue(component, value);
        }
    }

    private static void setSingleInputValue(final InputComponent<?, Object> input, final Object value) {
        final Object convertedType;
        if (value != null) {
            convertedType = convertToUIInputValue(input, value);
        } else {
            convertedType = null;
        }
        ((SingleValued) input).setValue(convertedType);
    }

    private static void setManyInputValue(final InputComponent<?, Object> input, Object value) {
        final List<Object> convertedValues;
        if (value != null) {
            convertedValues = new ArrayList<Object>();
            if (value instanceof Iterable) {
                Iterable<Object> values = (Iterable) value;
                for (Object obj : values) {
                    convertedValues.add(convertToUIInputValue(input, obj));
                }
            } else {
                convertedValues.add(convertToUIInputValue(input, value));
            }
        } else {
            convertedValues = null;
        }
        ((ManyValued) input).setValue(convertedValues);
    }

    /**
     * Returns the converted value that matches the input
     *
     * @param input
     * @param value
     * @return
     */
    private static Object convertToUIInputValue(final InputComponent<?, Object> input, final Object value) {
        final Object convertedType;
        ConverterFactory converterFactory = getConverterFactory();
        Class<Object> sourceType = (Class<Object>) value.getClass();
        Class<Object> targetType = input.getValueType();
        Converter<String, Object> valueConverter = input.getValueConverter();
        if (valueConverter != null) {
            Converter<Object, String> stringConverter = converterFactory.getConverter(sourceType, String.class);
            CompositeConverter compositeConverter = new CompositeConverter(stringConverter, valueConverter);
            convertedType = compositeConverter.convert(value);
        } else {
            Converter<Object, Object> converter = converterFactory.getConverter(sourceType, targetType);
            convertedType = converter.convert(value);
        }
        return convertedType;
    }

    public static ConverterFactory getConverterFactory() {
        return ForgeService.INSTANCE.lookup(ConverterFactory.class);
    }

    public static boolean hasValue(InputComponent<?, ?> input) {
        boolean ret;
        Object value = Inputs.getValueFor(input);
        if (value == null) {
            ret = false;
        } else if (value instanceof String && value.toString().isEmpty()) {
            ret = false;
        } else {
            ret = true;
        }
        return ret;
    }

    /**
     * Validate if the input has a value. If not, return the error message
     *
     * @param input
     * @return
     */
    public static String validateRequired(final InputComponent<?, ?> input) {
        String requiredMessage = null;
        if (input.isRequired() && !Inputs.hasValue(input)) {
            requiredMessage = input.getRequiredMessage();
            if (requiredMessage == null) {
                String labelValue = input.getLabel() == null ? input.getName() : input.getLabel();
                requiredMessage = labelValue + " is required!";
            }
        }
        return requiredMessage;
    }

    public static Converter<?, String> getItemLabelConverter(final SelectComponent<?, ?> input) {
        Converter<?, String> converter = input.getItemLabelConverter();
        if (converter == null) {
            ConverterFactory converterFactory = getConverterFactory();
            if (converterFactory != null) {
                converter = converterFactory.getConverter(input.getValueType(), String.class);
            }
        }
        return converter;
    }
}
