package org.jboss.tools.forge.aesh.f2;

import org.eclipse.swt.widgets.Composite;
import org.jboss.tools.forge.aesh.view.AeshTextViewer;

public class F2TextViewer extends AeshTextViewer {
	
    public F2TextViewer(Composite parent) {
    	super(parent);
    }
    
    protected void initializeConsole() {
    	aeshConsole = new F2Console();
    }
    
}
    
