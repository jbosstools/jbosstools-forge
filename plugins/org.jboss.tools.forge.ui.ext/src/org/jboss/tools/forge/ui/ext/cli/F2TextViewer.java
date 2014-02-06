package org.jboss.tools.forge.ui.ext.cli;

import org.eclipse.swt.widgets.Composite;
import org.jboss.tools.aesh.ui.view.AeshTextViewer;

public class F2TextViewer extends AeshTextViewer {
	
	private boolean constructionFinished = false;
	
    public F2TextViewer(Composite parent) {
    	super(parent);
    	constructionFinished = true;
    }
    
    protected void initializeConsole() {
    	aeshConsole = new F2Console();
    }
    
    protected void startConsole() {
    	if (constructionFinished) {
    		aeshConsole.start();
    	}
    }
    
}
    
