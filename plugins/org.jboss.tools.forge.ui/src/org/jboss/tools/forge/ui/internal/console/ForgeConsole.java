/**
 * Copyright (c) Red Hat, Inc., contributors and others 2013 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.forge.ui.internal.console;

import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.tools.forge.core.runtime.ForgeRuntime;

public interface ForgeConsole {

	String getLabel();

	Control createControl(Composite parent);

	IAction[] createActions();

	ForgeRuntime getRuntime();

	Resource<?> getCurrentResource();

	void goToPath(String path);
}
