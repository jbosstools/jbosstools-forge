/**
 * Copyright (c) Red Hat, Inc., contributors and others 2013 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.forge.ui.internal.console;

import java.beans.PropertyChangeListener;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.tools.forge.core.runtime.ForgeRuntime;
import org.jboss.tools.forge.ui.internal.viewer.ForgeTextViewer;

public abstract class AbstractForgeConsole implements ForgeConsole,
		PropertyChangeListener, DisposeListener {

	private ForgeRuntime runtime;
	private ForgeTextViewer textViewer;

	public AbstractForgeConsole(ForgeRuntime runtime) {
		this.runtime = runtime;
	}

	protected abstract ForgeTextViewer createTextViewer(Composite parent);

	protected ForgeTextViewer getTextViewer() {
		return textViewer;
	}

	public ForgeRuntime getRuntime() {
		return runtime;
	}

	public String getLabel() {
		return "Forge " + getRuntime().getVersion() + " - "
				+ getRuntime().getType().name().toLowerCase();
	}

	@Override
	public Control createControl(Composite parent) {
		if (textViewer == null || textViewer.getControl().isDisposed()) {
			textViewer = createTextViewer(parent);
		}
		Control result = textViewer.getControl();
		getRuntime().addPropertyChangeListener(this);
		result.addDisposeListener(this);
		return textViewer.getControl();
	}

	@Override
	public void widgetDisposed(DisposeEvent event) {
		if (event.getSource() == getTextViewer().getControl()) {
			getRuntime().removePropertyChangeListener(this);
		}
	}

	@Override
	public Resource<?> getCurrentResource() {
		throw new UnsupportedOperationException();
	}

}
