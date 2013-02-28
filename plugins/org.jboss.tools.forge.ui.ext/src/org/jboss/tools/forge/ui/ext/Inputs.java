/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.tools.forge.ui.ext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jboss.forge.convert.Converter;
import org.jboss.forge.convert.ConverterFactory;
import org.jboss.forge.ui.facets.HintsFacet;
import org.jboss.forge.ui.hints.InputType;
import org.jboss.forge.ui.input.UIInput;
import org.jboss.forge.ui.input.UIInputComponent;
import org.jboss.forge.ui.input.UISelectMany;
import org.jboss.forge.ui.input.UISelectOne;
import org.jboss.tools.forge.ext.core.ForgeService;

@SuppressWarnings({ "rawtypes", "unchecked" })
public abstract class Inputs {

    public static InputType getInputType(UIInputComponent<?, ?> input) {
        InputType result = null;
        if (input.hasFacet(HintsFacet.class)) {
            HintsFacet facet = input.getFacet(HintsFacet.class);
            result = facet.getInputType();
        }
        return result;
    }

    public static Object getValueFor(UIInputComponent<?, ?> component) {
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

    public static void setValueFor(UIInputComponent<?, ?> component, Object value) {
        if (component instanceof UIInput) {
            setInputValue((UIInput<Object>) component, value);
        } else if (component instanceof UISelectOne) {
            setInputValue((UISelectOne) component, value);
        } else if (component instanceof UISelectMany) {
            setInputValue((UISelectMany) component, value);
        }
    }

    private static void setInputValue(final UIInput<Object> input, Object value) {
        Object convertedType = value;
        if (value != null) {
            // TODO: Cache Converter ?
            ConverterFactory converterFactory = getConverterFactory();
            Class<? extends Object> source = value.getClass();
            Class<Object> target = input.getValueType();
            if (converterFactory != null) {
                Converter converter = converterFactory.getConverter(source, target);
                convertedType = converter.convert(value);
            } else {
                System.err.println("Converter Factory was not deployed !! Cannot convert from " + source + " to "
                    + target);
            }
        }
        input.setValue(convertedType);
    }

    private static void setInputValue(final UISelectOne<Object> input, Object value) {
        Object convertedType = value;
        if (value != null) {
            // TODO: Cache Converter ?
            ConverterFactory converterFactory = getConverterFactory();
            Class<? extends Object> source = value.getClass();
            Class<Object> target = input.getValueType();
            if (converterFactory != null) {
                Converter converter = converterFactory.getConverter(source, target);
                convertedType = converter.convert(value);
            } else {
                System.err.println("Converter Factory was not deployed !! Cannot convert from " + source + " to "
                    + target);
            }
        }
        input.setValue(convertedType);
    }

    private static void setInputValue(final UISelectMany<Object> input, Object value) {
        if (value != null) {
            // TODO: Cache Converter ?

            if (value instanceof Iterable) {
                Iterable<Object> values = (Iterable) value;

                List<Object> convertedValues = new ArrayList<Object>();
                for (Object object : values) {
                    convertedValues.add(convertValue(input.getValueType(), object));
                }
                input.setValue(values);
            } else {
                input.setValue(Arrays.asList(convertValue(input.getValueType(), value)));
            }

        }
    }

    public static <T> T convertValue(Class<T> targetType, Object value) {
        ConverterFactory converterFactory = getConverterFactory();
        Class<? extends Object> source = value.getClass();
        if (converterFactory != null) {
            Converter converter = converterFactory.getConverter(source, targetType);
            return (T) converter.convert(value);
        } else {
            System.err.println("Converter Factory was not deployed !! Cannot convert from " + source + " to "
                + targetType);
            return (T) value;
        }
    }

    public static ConverterFactory getConverterFactory() {
        return ForgeService.INSTANCE.lookup(ConverterFactory.class);
    }

    public static boolean hasValue(UIInputComponent<?, ?> input) {
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

    public static String validateRequired(final UIInputComponent<?, ?> input) {
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

}
