/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.tools.forge.ui.control;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.jboss.forge.ui.UIInput;
import org.jboss.forge.ui.hints.InputType;
import org.jboss.forge.ui.hints.InputTypes;
import org.jboss.tools.forge.ui.wizards.ForgeWizardPage;

public class EnumComboControlBuilder extends ControlBuilder {

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Control build(ForgeWizardPage page, final UIInput<Object> input,
			final Composite container) {
		final Combo combo = new Combo(container, SWT.BORDER | SWT.SINGLE
				| SWT.READ_ONLY);
		Enum[] enumConstants = input.getValueType().asSubclass(Enum.class)
				.getEnumConstants();
		for (Enum enum1 : enumConstants) {
			combo.add(enum1.name());
		}
		combo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int selectionIndex = combo.getSelectionIndex();
				if (selectionIndex != -1) {
					String item = combo.getItem(selectionIndex);
					Class enumType = input.getValueType();
					input.setValue(Enum.valueOf(enumType, item));
				}
			}
		});
		return combo;
	}

	@Override
	protected Class<?> getProducedType() {
		return Enum.class;
	}

	@Override
	protected InputType getSupportedInputType() {
		return InputTypes.SELECT_ONE;
	}
}
