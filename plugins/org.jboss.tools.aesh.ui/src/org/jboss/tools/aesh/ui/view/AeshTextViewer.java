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
import org.jboss.tools.aesh.ui.document.DelegateDocument;
import org.jboss.tools.aesh.ui.document.DelegateDocument.CursorListener;
import org.jboss.tools.aesh.ui.internal.FontManager;

public abstract class AeshTextViewer extends TextViewer {
	
	private static final String START_LINE = new Character((char)1).toString();
	private static final String PREV_CHAR = new Character((char)2).toString();
	private static final String CTRL_C = new Character((char)3).toString();
	private static final String CTRL_D = new Character((char)4).toString();
	private static final String END_LINE = new Character((char)5).toString();
	private static final String NEXT_CHAR = new Character((char)6).toString();
	private static final String DELETE_PREV_CHAR = new Character((char)8).toString();
	private static final String PREV_HISTORY = new Character((char)16).toString();
	private static final String NEXT_HISTORY = new Character((char)14).toString();
	private static final String DELETE_NEXT_CHAR = new String(new char[] {(char)27,(char)91,(char)51,(char)126});

	private Console console;
	private DelegateDocument aeshDocument;
	
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
    			StyleRange styleRange = aeshDocument.getCurrentStyleRange();
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
				console.connect(aeshDocument.getProxy());
		    	setDocument(aeshDocument);
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
						console.sendInput(END_LINE);
						break;
					case ST.LINE_START:
						console.sendInput(START_LINE);
						break;
					case ST.LINE_UP:
						console.sendInput(PREV_HISTORY);
						break;
					case ST.LINE_DOWN:
						console.sendInput(NEXT_HISTORY);
						break;
					case ST.COLUMN_PREVIOUS:
						console.sendInput(PREV_CHAR);
						break;
					case ST.COLUMN_NEXT:
						console.sendInput(NEXT_CHAR);
						break;
					case ST.DELETE_PREVIOUS:
						console.sendInput(DELETE_PREV_CHAR);
						break;
					case ST.DELETE_NEXT:
						console.sendInput(DELETE_NEXT_CHAR);
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
    	aeshDocument = new DelegateDocument();
    	aeshDocument.addCursorListener(cursorListener);
    	aeshDocument.addDocumentListener(documentListener);
    }
    
    private void initializeTextWidget() {
    	getTextWidget().setFont(JFaceResources.getFont(FontManager.AESH_CONSOLE_FONT));
    	getTextWidget().addVerifyKeyListener(new VerifyKeyListener() {			
			@Override
			public void verifyKey(VerifyEvent event) {
				if ((event.stateMask & SWT.CTRL) == SWT.CTRL ) {
					if (event.keyCode == 'd') {
						console.sendInput(CTRL_D);
					} else if (event.keyCode == 'c') {
						console.sendInput(CTRL_C);
					}
				}
			}
		});
    }
    
}
    
