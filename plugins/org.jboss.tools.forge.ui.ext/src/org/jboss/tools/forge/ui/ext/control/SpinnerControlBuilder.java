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
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.tools.forge.ext.core.FurnaceService;
import org.jboss.tools.forge.ui.ext.ForgeUIPlugin;
import org.jboss.tools.forge.ui.ext.wizards.ForgeWizardPage;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class SpinnerControlBuilder extends ControlBuilder<Spinner> {

	@SuppressWarnings("unchecked")
	@Override
	public Spinner build(final ForgeWizardPage page,
			final InputComponent<?, ?> input, final Composite container) {
		// Create the label
		Label label = new Label(container, SWT.NULL);
		label.setText(getMnemonicLabel(input, true));

		final Spinner txt = new Spinner(container, SWT.BORDER);
		// TODO: Ranges may be configurable in the future
		txt.setMinimum(Integer.MIN_VALUE);
		txt.setMaximum(Integer.MAX_VALUE);
		txt.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		txt.setToolTipText(input.getDescription());
		// Set Default Value
		final ConverterFactory converterFactory = FurnaceService.INSTANCE
				.getConverterFactory();
		if (converterFactory != null) {
			Converter<Object, Integer> converter = (Converter<Object, Integer>) converterFactory
					.getConverter(input.getValueType(), Integer.class);
			Integer value = converter.convert(InputComponents
					.getValueFor(input));
			txt.setSelection(value == null ? 0 : value);
		}

		txt.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				CommandController controller = page.getController();
				try {
					controller.setValueFor(input.getName(), txt.getText());
				} catch (Exception ex) {
					ForgeUIPlugin.log(ex);
					controller.setValueFor(input.getName(), null);
				}
			}
		});

		// skip the third column
		Label dummy = new Label(container, SWT.NONE);
		dummy.setText("");

		return txt;
	}

	@Override
	public void setEnabled(Spinner control, boolean enabled) {
		control.setEnabled(enabled);
	}

	@Override
	protected Class<?> getProducedType() {
		return Integer.class;
	}

	@Override
	protected String getSupportedInputType() {
		return InputType.DEFAULT;
	}

	@Override
	protected Class<?>[] getSupportedInputComponentTypes() {
		return new Class[] { UIInput.class };
	}

	@Override
	public Control[] getModifiableControlsFor(Spinner control) {
		return new Control[] { control };
	}

}
