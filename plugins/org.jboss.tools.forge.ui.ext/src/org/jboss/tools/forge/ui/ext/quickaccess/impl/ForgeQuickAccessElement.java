/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.tools.forge.ui.ext.quickaccess.impl;

import org.eclipse.jface.resource.ImageDescriptor;
import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.tools.forge.ui.ext.ForgeUIPlugin;
import org.jboss.tools.forge.ui.ext.quickaccess.QuickAccessElement;

public class ForgeQuickAccessElement extends QuickAccessElement {
	private String label;
	private String tooltip;
	private UICommand command;

	public ForgeQuickAccessElement(ForgeQuickAccessProvider provider,
			UICommand command) {
		super(provider);
		this.command = command;

		UICommandMetadata metadata = command.getMetadata();
		this.label = metadata.getName();
		this.tooltip = metadata.getDescription();
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return ForgeUIPlugin.getForgeIcon();
	}

	@Override
	public String getId() {
		return getLabel();
	}

	@Override
	public void execute() {
	}

	public UICommand getCommand() {
		return command;
	}

	@Override
	public String getTooltip() {
		return this.tooltip;
	}
}
