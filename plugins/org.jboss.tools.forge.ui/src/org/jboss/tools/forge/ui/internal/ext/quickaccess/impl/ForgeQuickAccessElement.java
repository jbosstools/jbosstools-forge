/**
 * Copyright (c) Red Hat, Inc., contributors and others 2004 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.forge.ui.internal.ext.quickaccess.impl;

import org.eclipse.jface.resource.ImageDescriptor;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.tools.forge.ui.internal.ForgeUIPlugin;
import org.jboss.tools.forge.ui.internal.ext.quickaccess.QuickAccessElement;

public class ForgeQuickAccessElement extends QuickAccessElement {
	private String label;
	private String tooltip;
	private UICommand command;

	public ForgeQuickAccessElement(ForgeQuickAccessProvider provider, UIContext context,
			UICommand command) {
		super(provider);
		this.command = command;

		UICommandMetadata metadata = command.getMetadata(context);
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ForgeQuickAccessElement other = (ForgeQuickAccessElement) obj;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		return true;
	}
}
