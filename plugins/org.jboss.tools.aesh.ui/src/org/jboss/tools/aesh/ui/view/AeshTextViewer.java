package org.jboss.tools.aesh.ui.view;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ST;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.jboss.tools.aesh.core.console.Console;
import org.jboss.tools.aesh.ui.internal.document.CursorListener;
import org.jboss.tools.aesh.ui.internal.document.DelegatingDocument;
import org.jboss.tools.aesh.ui.internal.document.StyleImpl;
import org.jboss.tools.aesh.ui.internal.util.CharacterConstants;
import org.jboss.tools.aesh.ui.internal.util.FontManager;

public abstract class AeshTextViewer extends TextViewer {
	
	private Console console;
	private DelegatingDocument aeshDocument;
	
	private CursorListener cursorListener = new CursorListener() {		
		@Override
		public void cursorMoved() {
			StyledText textWidget = getTextWidget();
			if (textWidget != null && !textWidget.isDisposed()) {
				textWidget.setCaretOffset(aeshDocument.getCursorOffset());
			}
		}
	};
	
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
                StyleImpl style = aeshDocument.getCurrentStyleRange();
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
				console.connect(aeshDocument);
		    	setDocument(aeshDocument.getDelegate());
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
		    	aeshDocument.reset();
		    	setDocument(null);    	
			}   		
    	});
    }
    
    protected abstract Console createConsole();
    
	protected StyledText createTextWidget(Composite parent, int styles) {
		StyledText styledText= new StyledText(parent, styles) {
			public void invokeAction(int action) {
				switch (action) {
					case ST.LINE_END:
						console.sendInput(CharacterConstants.END_LINE);
						break;
					case ST.LINE_START:
						console.sendInput(CharacterConstants.START_LINE);
						break;
					case ST.LINE_UP:
						console.sendInput(CharacterConstants.PREV_HISTORY);
						break;
					case ST.LINE_DOWN:
						console.sendInput(CharacterConstants.NEXT_HISTORY);
						break;
					case ST.COLUMN_PREVIOUS:
						console.sendInput(CharacterConstants.PREV_CHAR);
						break;
					case ST.COLUMN_NEXT:
						console.sendInput(CharacterConstants.NEXT_CHAR);
						break;
					case ST.DELETE_PREVIOUS:
						console.sendInput(CharacterConstants.DELETE_PREV_CHAR);
						break;
					case ST.DELETE_NEXT:
						console.sendInput(CharacterConstants.DELETE_NEXT_CHAR);
						break;
					default: super.invokeAction(action);
				}
			}
		};
		styledText.setLeftMargin(Math.max(styledText.getLeftMargin(), 2));
		return styledText;
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
    	aeshDocument = new DelegatingDocument();
    	aeshDocument.addCursorListener(cursorListener);
    	aeshDocument.getDelegate().addDocumentListener(documentListener);
    }
    
    private void initializeTextWidget() {
    	getTextWidget().setFont(JFaceResources.getFont(FontManager.AESH_CONSOLE_FONT));
    	getTextWidget().addVerifyKeyListener(new VerifyKeyListener() {			
			@Override
			public void verifyKey(VerifyEvent event) {
				if ((event.stateMask & SWT.CTRL) == SWT.CTRL ) {
					if (event.keyCode == 'd') {
						console.sendInput(CharacterConstants.CTRL_D);
					} else if (event.keyCode == 'c') {
						console.sendInput(CharacterConstants.CTRL_C);
					}
				}
			}
		});
    }
    
}
    
