/**
 * Copyright (c) Red Hat, Inc., contributors and others 2013 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.aesh.example;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.jboss.tools.aesh.ui.view.AbstractTextViewer;

public class ExampleView extends ViewPart {
	
	public static final String ID = "org.jboss.tools.forge.aesh.example";

	private AbstractTextViewer textViewer;	
	
	@Override
	public void createPartControl(Composite parent) {
		textViewer = new ExampleTextViewer(parent);
		textViewer.startConsole();
	}

	@Override
	public void setFocus() {
		textViewer.getControl().setFocus();
	}
	
	@Override
	public void dispose() {
		textViewer.stopConsole();
		super.dispose();
	}

}
