/**
 * Copyright (c) Red Hat, Inc., contributors and others 2013 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.forge.ui.internal.ext.control;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.forge.furnace.proxy.Proxies;
import org.jboss.tools.forge.core.furnace.FurnaceService;
import org.jboss.tools.forge.ui.internal.ext.wizards.ForgeWizardPage;

public class RadioControlBuilder extends ControlBuilder<Control> {

	@Override
	@SuppressWarnings({ "unchecked" })
	public Control build(final ForgeWizardPage page,
			final InputComponent<?, ?> input, final String inputName,
			final Composite parent) {
		// Create the label
		Label label = new Label(parent, SWT.NULL);
		label.setText(getMnemonicLabel(input, true));

		Composite container = new Composite(parent, SWT.NULL);
		container.setLayout(new RowLayout());
		container.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		final ConverterFactory converterFactory = FurnaceService.INSTANCE
				.getConverterFactory();
		UISelectOne<Object> selectOne = (UISelectOne<Object>) input;
		Converter<Object, String> itemLabelConverter = (Converter<Object, String>) InputComponents
				.getItemLabelConverter(converterFactory, selectOne);
		Object originalValue = InputComponents.getValueFor(input);
		Iterable<Object> valueChoices = selectOne.getValueChoices();
		if (valueChoices != null) {
			for (final Object choice : valueChoices) {
				final String itemLabel = itemLabelConverter.convert(choice);
				final Button button = new Button(container, SWT.RADIO);
				button.setText(itemLabel);
				button.setToolTipText(input.getDescription());
				boolean selected = Proxies.areEquivalent(choice, originalValue);
				button.setSelection(selected);
				button.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						if (button.getSelection()) {
							final CommandController controller = page
									.getController();
							controller.setValueFor(inputName,
									Proxies.unwrap(choice));
						}
					}
				});
			}
		}

		// skip the thrid column
		Label dummy = new Label(parent, SWT.NONE);
		dummy.setText("");

		return container;
	}

	@Override
	protected Class<Object> getProducedType() {
		return Object.class;
	}

	@Override
	protected String getSupportedInputType() {
		return InputType.RADIO;
	}

	@Override
	protected Class<?>[] getSupportedInputComponentTypes() {
		return new Class<?>[] { UISelectOne.class };
	}
}
