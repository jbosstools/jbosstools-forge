/**
 * Copyright (c) Red Hat, Inc., contributors and others 2013 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.forge.ui.internal.ext.wizards;

import org.jboss.tools.forge.ui.internal.ext.context.UIContextImpl;

public interface WizardListener {

	public void onFinish(UIContextImpl context);

	public void dispose();
}
