/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.tools.forge.ui.ext.control;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.tools.forge.ext.core.FurnaceService;
import org.jboss.tools.forge.ui.ext.wizards.ForgeWizardPage;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class SpinnerControlBuilder extends ControlBuilder {

	@Override
	public Spinner build(ForgeWizardPage page,
			final InputComponent<?, Object> input, final Composite container) {
		// Create the label
		Label label = new Label(container, SWT.NULL);
		label.setText(getMnemonicLabel(input, true));

		final Spinner txt = new Spinner(container, SWT.BORDER | SWT.SINGLE);
		txt.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		txt.setToolTipText(input.getDescription());
		// Set Default Value
		final ConverterFactory converterFactory = FurnaceService.INSTANCE
				.getConverterFactory();
		if (converterFactory != null) {
			Converter<Object, Integer> converter = converterFactory
					.getConverter(input.getValueType(), Integer.class);
			Integer value = converter.convert(InputComponents
					.getValueFor(input));
			txt.setSelection(value == null ? 0 : value);
		}

		txt.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				InputComponents.setValueFor(converterFactory, input,
						txt.getText());
			}
		});
		return txt;
	}

	@Override
	protected Class<?> getProducedType() {
		return Number.class;
	}

	@Override
	protected InputType getSupportedInputType() {
		return InputType.TEXTBOX;
	}

	@Override
	protected Class<?>[] getSupportedInputComponentTypes() {
		return new Class[] { UIInput.class };
	}

	@Override
	public Control[] getModifiableControlsFor(Control control) {
		return new Control[] { control };
	}

}
