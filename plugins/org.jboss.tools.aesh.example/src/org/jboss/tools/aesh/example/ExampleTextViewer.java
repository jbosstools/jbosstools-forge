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
