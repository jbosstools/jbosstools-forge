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
import org.jboss.tools.aesh.core.console.Console;
import org.jboss.tools.aesh.ui.document.DelegateDocument;
import org.jboss.tools.aesh.ui.document.DelegateDocument.CursorListener;
import org.jboss.tools.aesh.ui.internal.fonts.FontManager;

public abstract class AeshTextViewer extends TextViewer {
	
	private static String START_LINE = new Character((char)1).toString();
	private static String PREV_CHAR = new Character((char)2).toString();
	private static String CTRL_C = new Character((char)3).toString();
	private static String CTRL_D = new Character((char)4).toString();
	private static String END_LINE = new Character((char)5).toString();
	private static String NEXT_CHAR = new Character((char)6).toString();
	private static String DELETE_PREV_CHAR = new Character((char)8).toString();
	private static String PREV_HISTORY = new Character((char)16).toString();
	private static String NEXT_HISTORY = new Character((char)14).toString();
	private static String DELETE_NEXT_CHAR = new String(new char[] {(char)27,(char)91,(char)51,(char)126});

	protected Console aeshConsole;
	protected DelegateDocument aeshDocument;
	
	protected CursorListener cursorListener = new CursorListener() {		
		@Override
		public void cursorMoved() {
			StyledText textWidget = getTextWidget();
			if (textWidget != null && !textWidget.isDisposed()) {
				textWidget.setCaretOffset(aeshDocument.getCursorOffset());
			}
		}
	};
	
	protected IDocumentListener documentListener = new IDocumentListener() {
    	@Override
        public void documentAboutToBeChanged(DocumentEvent event) {
        }
        @Override
        public void documentChanged(final DocumentEvent event) {
            StyledText textWidget = getTextWidget();
            if (textWidget != null && !textWidget.isDisposed()) {
                int lineCount = textWidget.getLineCount();
                textWidget.setTopIndex(lineCount - 1);
    			StyleRange styleRange = getDocument().getCurrentStyleRange();
    			if (styleRange != null && event.getLength() == 0) {
     				textWidget.setStyleRange(styleRange);
    			}
            }
        }
    };
	
    public AeshTextViewer(Composite parent) {
    	super(parent, SWT.WRAP | SWT.V_SCROLL | SWT.H_SCROLL);
    	initialize();
    }
    
    protected abstract void initializeConsole();
    
    protected void initializeDocument() {
    	aeshDocument = new DelegateDocument();
    	aeshDocument.addCursorListener(cursorListener);
    	aeshDocument.addDocumentListener(documentListener);
    	aeshConsole.connect(aeshDocument.getProxy());
    }
    
    protected void initializeTextWidget() {
    	getTextWidget().setFont(JFaceResources.getFont(FontManager.AESH_CONSOLE_FONT));
    	getTextWidget().addVerifyKeyListener(new VerifyKeyListener() {			
			@Override
			public void verifyKey(VerifyEvent event) {
				if ((event.stateMask & SWT.CTRL) == SWT.CTRL ) {
					if (event.keyCode == 'd') {
						aeshConsole.sendInput(CTRL_D);
					} else if (event.keyCode == 'c') {
						aeshConsole.sendInput(CTRL_C);
					}
				}
			}
		});
    }
    
    public void startConsole() {
    	setDocument(aeshDocument);
    	aeshConsole.start();
    }
    
    protected void initialize() {
    	initializeConsole();
    	initializeDocument();
    	initializeTextWidget();
    }
    
	protected StyledText createTextWidget(Composite parent, int styles) {
		StyledText styledText= new StyledText(parent, styles) {
			public void invokeAction(int action) {
				switch (action) {
					case ST.LINE_END:
						aeshConsole.sendInput(END_LINE);
						break;
					case ST.LINE_START:
						aeshConsole.sendInput(START_LINE);
						break;
					case ST.LINE_UP:
						aeshConsole.sendInput(PREV_HISTORY);
						break;
					case ST.LINE_DOWN:
						aeshConsole.sendInput(NEXT_HISTORY);
						break;
					case ST.COLUMN_PREVIOUS:
						aeshConsole.sendInput(PREV_CHAR);
						break;
					case ST.COLUMN_NEXT:
						aeshConsole.sendInput(NEXT_CHAR);
						break;
					case ST.DELETE_PREVIOUS:
						aeshConsole.sendInput(DELETE_PREV_CHAR);
						break;
					case ST.DELETE_NEXT:
						aeshConsole.sendInput(DELETE_NEXT_CHAR);
						break;
					default: super.invokeAction(action);
				}
			}
		};
		styledText.setLeftMargin(Math.max(styledText.getLeftMargin(), 2));
		return styledText;
	}

    public void cleanup() {
    	aeshConsole.stop();
    	aeshDocument.removeDocumentListener(documentListener);
    	aeshDocument.removeCursorListener(cursorListener);
    	aeshConsole.disconnect();
    }
    
    protected void handleVerifyEvent(VerifyEvent e) {
    	aeshConsole.sendInput(e.text);
		e.doit = false;    	
    }
    
    public DelegateDocument getDocument() {
    	return (DelegateDocument)super.getDocument();
    }
    
}
    
