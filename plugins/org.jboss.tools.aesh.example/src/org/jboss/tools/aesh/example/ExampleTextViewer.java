package org.jboss.tools.aesh.example;

import org.eclipse.swt.widgets.Composite;
import org.jboss.tools.aesh.ui.view.AeshTextViewer;

public class ExampleTextViewer extends AeshTextViewer {

	public ExampleTextViewer(Composite parent) {
		super(parent);
	}

	@Override
    protected void initializeConsole() {
    	aeshConsole = new ExampleConsole();
    }
    
    protected void initialize() {
    	super.initialize();
    	((ExampleConsole)aeshConsole).connect(aeshDocument.getProxy());
    }

}
