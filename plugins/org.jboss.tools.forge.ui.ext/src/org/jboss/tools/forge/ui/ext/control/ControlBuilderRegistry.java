/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.tools.forge.ui.ext.control;

import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.tools.forge.ui.ext.control.many.CheckboxTableControlBuilder;
import org.jboss.tools.forge.ui.ext.control.many.DirectoryChooserMultipleControlBuilder;
import org.jboss.tools.forge.ui.ext.control.many.FileChooserMultipleControlBuilder;
import org.jboss.tools.forge.ui.ext.control.many.JavaClassChooserMultipleControlBuilder;
import org.jboss.tools.forge.ui.ext.control.many.TextBoxMultipleControlBuilder;

/**
 * A factory for {@link ControlBuilder} instances.
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */
public class ControlBuilderRegistry {

	private static final ControlBuilder[] CONTROL_BUILDERS = {
			new CheckboxControlBuilder(),
			new ComboControlBuilder(),
			new RadioControlBuilder(),
			new FileChooserControlBuilder(),
			new DirectoryChooserControlBuilder(),
			new CheckboxTableControlBuilder(),
			new TextBoxControlBuilder(),
			new SpinnerControlBuilder(),
			new PasswordTextBoxControlBuilder(),
			new JavaPackageChooserControlBuilder(),
			new JavaClassChooserControlBuilder(),
			new TextBoxMultipleControlBuilder(),
			new FileChooserMultipleControlBuilder(),
			new DirectoryChooserMultipleControlBuilder(),
			new JavaClassChooserMultipleControlBuilder(),
			new FallbackTextBoxControlBuilder() };

	public static ControlBuilder getBuilderFor(InputComponent<?, ?> input) {
		for (ControlBuilder builder : CONTROL_BUILDERS) {
			if (builder.handles(input)) {
				return builder;
			}
		}
		throw new IllegalArgumentException(
				"No UI component found for input type of "
						+ input.getValueType());
	}
}
