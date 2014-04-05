/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.tools.forge.ui.ext.listeners;

import java.util.ArrayList;
import java.util.List;

import org.jboss.tools.forge.ui.ext.context.UIContextImpl;
import org.jboss.tools.forge.ui.ext.wizards.ForgeWizard;
import org.jboss.tools.forge.ui.ext.wizards.WizardListener;

/**
 * Fires events related to wizard manipulation. Used in {@link ForgeWizard}
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
public enum EventBus {

	INSTANCE;

	private List<WizardListener> wizardListeners = new ArrayList<WizardListener>();

	public void register(WizardListener wizardListener) {
		wizardListeners.add(wizardListener);
	}

	public void fireWizardFinished(final UIContextImpl context) {
		for (WizardListener listener : wizardListeners) {
			listener.onFinish(context);
			listener.dispose();
		}
	}

	public void fireWizardClosed(UIContextImpl context) {
		for (WizardListener listener : wizardListeners) {
			listener.dispose();
		}
	}

	public void clearListeners() {
		wizardListeners.clear();
	}
}
