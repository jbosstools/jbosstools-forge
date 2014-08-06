/**
 * Copyright (c) Red Hat, Inc., contributors and others 2004 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.forge.ui.internal.viewer;

import org.eclipse.swt.widgets.Control;
import org.jboss.tools.aesh.core.console.Console;

public interface ForgeTextViewer {
	
	Control getControl();
	void startConsole();
	void stopConsole();
	Console getConsole();

}
