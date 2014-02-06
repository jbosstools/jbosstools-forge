package org.jboss.tools.forge.ui.ext.cli;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
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
    
    public void startConsole() {
    	Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
		    	if (constructionFinished) {
		    		setDocument(aeshDocument);
		    		aeshConsole.start();
		    	}
			}   		
    	});
    }
    
    public void stopConsole() {
    	Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
		    	aeshConsole.stop();
		    	aeshDocument.set("");
		    	setDocument(null);
			}    		
    	});
    }
    
    
}
    
