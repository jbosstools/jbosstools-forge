/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.tools.forge.ui.ext.wizards;

import org.jboss.tools.forge.ui.ext.context.UIContextImpl;

public interface WizardListener {

	public void onFinish(UIContextImpl context);

	public void dispose();
}
