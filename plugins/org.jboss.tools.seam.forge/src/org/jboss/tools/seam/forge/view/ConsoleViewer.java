package org.jboss.tools.seam.forge.view;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.console.TextConsoleViewer;
import org.jboss.tools.seam.forge.console.Console;

public class ConsoleViewer extends TextConsoleViewer {
	
	private static String BACKSPACE = new String(new byte[] {'\b'});
//	private static String UP_ARROW = new String(new byte[] { 0x1b, 0x5b, 0x41 });
//	private static String DOWN_ARROW = new String(new byte[] { 0x1b, 0x5b, 0x42 });

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
    
    private void handleBackspace() {
    	console.getInputStream().appendData(BACKSPACE);
    }
    
    private void handleArrowUp() {
//    	System.out.println("handle arrow up");
//    	console.getInputStream().appendData(UP_ARROW);
    }
    
    private void handleArrowDown() {
//    	System.out.println("handle arrow down");
//    	console.getInputStream().appendData(DOWN_ARROW);
    }
    
	protected StyledText createTextWidget(Composite parent, int styles) {
		ConsoleText styledText= new ConsoleText(parent, styles);
		styledText.setLeftMargin(Math.max(styledText.getLeftMargin(), 2));
		styledText.addKeyListener(new ConsoleKeyListener());
		return styledText;
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
	
	private class ConsoleKeyListener implements KeyListener {
		
		public void keyPressed(KeyEvent e) {
		}

		public void keyReleased(KeyEvent e) {
			if (e.keyCode == SWT.BS) {
				handleBackspace();
			} else if (e.keyCode == SWT.ARROW_UP) {
				handleArrowUp();
			} else if (e.keyCode == SWT.ARROW_DOWN) {
				handleArrowDown();
			}
		}
		
	}
   
}
