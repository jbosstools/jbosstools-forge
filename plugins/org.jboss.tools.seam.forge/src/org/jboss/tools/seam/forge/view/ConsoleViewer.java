package org.jboss.tools.seam.forge.view;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.console.TextConsoleViewer;
import org.jboss.tools.seam.forge.console.Console;

public class ConsoleViewer extends TextConsoleViewer {

    private Console console = null;

    public ConsoleViewer(Composite parent, Console console) {
        super(parent, console);
        this.console = console;
        getDocument().addDocumentListener(new DocumentListener());
    }

    protected void handleVerifyEvent(VerifyEvent e) {
    	console.getInputStream().appendData(e.text);
    	e.doit = false;
    }
    
    private class DocumentListener implements IDocumentListener {
    	
        public void documentAboutToBeChanged(DocumentEvent event) {
        }

        public void documentChanged(DocumentEvent event) {
            revealEndOfDocument();
            Control control = getControl();
            if (control instanceof StyledText) {
            	StyledText text = (StyledText)control;
            	text.setCaretOffset(text.getCharCount());
            }
        }
    }
   
}
