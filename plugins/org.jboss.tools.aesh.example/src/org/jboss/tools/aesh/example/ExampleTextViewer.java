/**
 * Copyright (c) Red Hat, Inc., contributors and others 2004 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.aesh.example;

import org.eclipse.swt.widgets.Composite;
import org.jboss.tools.aesh.core.console.Console;
import org.jboss.tools.aesh.ui.view.AbstractTextViewer;

public class ExampleTextViewer extends AbstractTextViewer {

	public ExampleTextViewer(Composite parent) {
		super(parent);
	}

	@Override
    protected Console createConsole() {
		return new ExampleConsole();
    }
    
}
