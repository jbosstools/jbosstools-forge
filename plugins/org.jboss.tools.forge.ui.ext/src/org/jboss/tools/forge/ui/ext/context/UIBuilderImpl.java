/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.tools.forge.ui.ext.context;

import java.util.ArrayList;
import java.util.List;

import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.furnace.util.Assert;

public class UIBuilderImpl implements UIBuilder {
	private List<InputComponent<?, Object>> inputs = new ArrayList<InputComponent<?, Object>>();
	private UIContext context;

	public UIBuilderImpl(UIContext context) {
		this.context = context;
	}

	@SuppressWarnings("unchecked")
	@Override
	public UIBuilder add(InputComponent<?, ?> input) {
		Assert.notNull(input, "Input must not be null");
		inputs.add((InputComponent<?, Object>) input);
		return this;
	}

	public List<InputComponent<?, Object>> getInputs() {
		return inputs;
	}

	@Override
	public UIContext getUIContext() {
		return context;
	}
}
