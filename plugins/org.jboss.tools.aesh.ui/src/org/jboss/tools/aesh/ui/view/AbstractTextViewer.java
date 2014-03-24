package org.jboss.tools.aesh.ui.view;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.jboss.tools.aesh.core.console.Console;
import org.jboss.tools.aesh.ui.internal.document.DocumentImpl;
import org.jboss.tools.aesh.ui.internal.util.FontManager;
import org.jboss.tools.aesh.ui.internal.viewer.CursorListenerImpl;
import org.jboss.tools.aesh.ui.internal.viewer.DocumentListenerImpl;
import org.jboss.tools.aesh.ui.internal.viewer.TextWidget;
import org.jboss.tools.aesh.ui.internal.viewer.VerifyKeyListenerImpl;

public abstract class AbstractTextViewer extends TextViewer {
	
	private Console console;
	private DocumentImpl document;
	private TextWidget textWidget;
	
    public AbstractTextViewer(Composite parent) {
    	super(parent, SWT.WRAP | SWT.V_SCROLL | SWT.H_SCROLL);
    	initialize();
    }
    
    public void startConsole() {
    	Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				console.connect(document);
		    	setDocument(document.getDelegate());
		    	console.start();
			}   		
    	});
    }
    
    public void stopConsole() {
    	Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
		    	console.stop();
		    	console.disconnect();
		    	document.reset();
		    	setDocument(null);    	
			}   		
    	});
    }
    
    protected abstract Console createConsole();
    
	protected StyledText createTextWidget(Composite parent, int styles) {
		textWidget = new TextWidget(parent, styles);
		return textWidget;
	}

    protected void handleVerifyEvent(VerifyEvent e) {
    	console.sendInput(e.text);
		e.doit = false;    	
    }
    
    private void initialize() {
    	initializeConsole();
    	initializeDocument();
    	initializeTextWidget();
    }
    
    private void initializeConsole() {
    	console = createConsole();
    }
    
    private void initializeDocument() {
    	document = new DocumentImpl();
    	document.setCursorListener(new CursorListenerImpl(textWidget, document));
    	document.setDocumentListener(new DocumentListenerImpl(textWidget, document));
    }
    
    private void initializeTextWidget() {
    	textWidget.setConsole(console);
    	textWidget.setFont(JFaceResources.getFont(FontManager.AESH_CONSOLE_FONT));
    	textWidget.addVerifyKeyListener(new VerifyKeyListenerImpl(console));
    }
    
}
    
