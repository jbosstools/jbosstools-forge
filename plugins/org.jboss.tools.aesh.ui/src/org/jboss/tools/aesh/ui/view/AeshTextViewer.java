package org.jboss.tools.aesh.ui.view;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.jboss.tools.aesh.core.console.Console;
import org.jboss.tools.aesh.ui.internal.document.DocumentImpl;
import org.jboss.tools.aesh.ui.internal.document.StyleImpl;
import org.jboss.tools.aesh.ui.internal.util.FontManager;
import org.jboss.tools.aesh.ui.internal.viewer.CursorListenerImpl;
import org.jboss.tools.aesh.ui.internal.viewer.TextWidget;
import org.jboss.tools.aesh.ui.internal.viewer.VerifyKeyListenerImpl;

public abstract class AeshTextViewer extends TextViewer {
	
	private Console console;
	private DocumentImpl document;
	private TextWidget textWidget;
	
	private IDocumentListener documentListener = new IDocumentListener() {
    	@Override
        public void documentAboutToBeChanged(DocumentEvent event) {
        }
        @Override
        public void documentChanged(final DocumentEvent event) {
            StyledText textWidget = getTextWidget();
            if (textWidget != null && !textWidget.isDisposed()) {
                int lineCount = textWidget.getLineCount();
                textWidget.setTopIndex(lineCount - 1);
                StyleImpl style = document.getCurrentStyleRange();
    			StyleRange styleRange = style.getStyleRange();
    			if (styleRange != null && 
    					event.getLength() == 0 && 
    					styleRange.start <= getDocument().getLength() && 
    					styleRange.length >= 0 && 
    					styleRange.start + styleRange.length <= getDocument().getLength()) {
     				textWidget.setStyleRange(styleRange);
    			}
            }
        }
    };
	
    public AeshTextViewer(Composite parent) {
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
    	document.getDelegate().addDocumentListener(documentListener);
    }
    
    private void initializeTextWidget() {
    	textWidget.setConsole(console);
    	textWidget.setFont(JFaceResources.getFont(FontManager.AESH_CONSOLE_FONT));
    	textWidget.addVerifyKeyListener(new VerifyKeyListenerImpl(console));
    }
    
}
    
