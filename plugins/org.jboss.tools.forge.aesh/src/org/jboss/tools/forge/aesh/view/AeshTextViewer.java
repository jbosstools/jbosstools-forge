package org.jboss.tools.forge.aesh.view;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.widgets.Composite;

public class AeshTextViewer extends TextViewer {
	
	private static final String AESH_CONSOLE_FONT = "org.jboss.tools.forge.aesh.font";

	private AeshConsole aeshConsole;
	private AeshDocument aeshDocument;
	
    public AeshTextViewer(Composite parent) {
    	super(parent, SWT.WRAP | SWT.V_SCROLL | SWT.H_SCROLL);
    	initialize();
    }
    
    private void initialize() {
    	aeshConsole = new AeshConsole();
    	aeshDocument = new AeshDocument();
    	aeshDocument.connect(aeshConsole);
    	setDocument(aeshDocument);
    	getTextWidget().setFont(JFaceResources.getFont(AESH_CONSOLE_FONT));
    	aeshConsole.start();
    }
    
    public void cleanup() {
    	aeshConsole.stop();
    	aeshDocument.disconnect();
    }
    
    protected void handleVerifyEvent(VerifyEvent e) {
    	aeshConsole.sendInput(e.text);
		e.doit = false;    	
    }
    
}
    
