package org.jboss.tools.forge.ui.internal.part;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ST;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.jboss.tools.forge.core.runtime.ForgeRuntime;
import org.jboss.tools.forge.ui.internal.document.ForgeDocument;

public class F1TextViewer extends TextViewer {

	private static String START_LINE = new Character((char)1).toString();
	private static String PREV_CHAR = new Character((char)2).toString();
	private static String CTRL_C = new Character((char)3).toString();
	private static String CTRL_D = new Character((char)4).toString();
	private static String END_LINE = new Character((char)5).toString();
	private static String NEXT_CHAR = new Character((char)6).toString();
	private static String DELETE_PREV_CHAR = new Character((char)8).toString();
	private static String PREV_HISTORY = new Character((char)16).toString();
	private static String NEXT_HISTORY = new Character((char)14).toString();
	private static String DELETE_NEXT_CHAR = new Character((char)127).toString();
	
	private static final String FORGE_CONSOLE_FONT = "org.jboss.tools.forge.console.font";
	
	private ForgeDocument forgeDocument = new ForgeDocument();
	private ForgeRuntime runtime = null;
	
	private class DocumentListener implements IDocumentListener, ForgeDocument.CursorListener {
    	@Override
        public void documentAboutToBeChanged(DocumentEvent event) {
        }
        @Override
        public void documentChanged(final DocumentEvent event) {
            StyledText textWidget = getTextWidget();
            if (textWidget != null && !textWidget.isDisposed()) {
                int lineCount = textWidget.getLineCount();
                textWidget.setTopIndex(lineCount - 1);
    			StyleRange styleRange = forgeDocument.getCurrentStyleRange();
    			if (styleRange != null) {
    				textWidget.setStyleRange(styleRange);
    			}
            }
        }
		@Override
		public void cursorMoved() {
			StyledText textWidget = getTextWidget();
			if (textWidget != null && !textWidget.isDisposed()) {
				textWidget.setCaretOffset(forgeDocument.getCursorOffset());
			}
		}
    }
	
    private DocumentListener documentListener = new DocumentListener();
    private IPropertyChangeListener fontListener = new IPropertyChangeListener() {		
		@Override
		public void propertyChange(PropertyChangeEvent event) {
			if (FORGE_CONSOLE_FONT.equals(event.getProperty())) {
				getTextWidget().setFont(JFaceResources.getFont(FORGE_CONSOLE_FONT));
			}
		}
	};
    
    public F1TextViewer(Composite parent, ForgeRuntime runtime) {
    	super(parent, SWT.WRAP | SWT.V_SCROLL | SWT.H_SCROLL);
    	this.runtime = runtime;
    	initialize();
    }

    private void initialize() {
        initDocument();
        initViewer();
        initFontListener();
    }
    
    private void initFontListener() {
    	JFaceResources.getFontRegistry().addListener(fontListener);
    }
        
    private void initDocument() {
        forgeDocument.addDocumentListener(documentListener);
        forgeDocument.addCursorListener(documentListener);
    	setDocument(forgeDocument);
    }
    
    private void initViewer() {
		getTextWidget().setStyleRanges(forgeDocument.getStyleRanges());
    	getTextWidget().setFont(JFaceResources.getFont(FORGE_CONSOLE_FONT));
    	getTextWidget().addVerifyKeyListener(new VerifyKeyListener() {			
			@Override
			public void verifyKey(VerifyEvent event) {
				if ((event.stateMask & SWT.CTRL) == SWT.CTRL ) {
					if (event.keyCode == 'd') {
						getRuntime().sendInput(CTRL_D);
					} else if (event.keyCode == 'c') {
						getRuntime().sendInput(CTRL_C);
					}
				}
			}
		});
    }
    
    protected void handleDispose() {
    	forgeDocument.removeCursorListener(documentListener);
    	forgeDocument.removeDocumentListener(documentListener);
    	JFaceResources.getFontRegistry().removeListener(fontListener);
    	super.handleDispose();
    }
    
	protected StyledText createTextWidget(Composite parent, int styles) {
		StyledText styledText= new StyledText(parent, styles) {
			public void invokeAction(int action) {
				switch (action) {
					case ST.LINE_END:
						getRuntime().sendInput(END_LINE);
						break;
					case ST.LINE_START:
						getRuntime().sendInput(START_LINE);
						break;
					case ST.LINE_UP:
						getRuntime().sendInput(PREV_HISTORY);
						break;
					case ST.LINE_DOWN:
						getRuntime().sendInput(NEXT_HISTORY);
						break;
					case ST.COLUMN_PREVIOUS:
						getRuntime().sendInput(PREV_CHAR);
						break;
					case ST.COLUMN_NEXT:
						getRuntime().sendInput(NEXT_CHAR);
						break;
					case ST.DELETE_PREVIOUS:
						getRuntime().sendInput(DELETE_PREV_CHAR);
						break;
					case ST.DELETE_NEXT:
						getRuntime().sendInput(DELETE_NEXT_CHAR);
						break;
					default: super.invokeAction(action);
				}
			}
		};
		styledText.setLeftMargin(Math.max(styledText.getLeftMargin(), 2));
		return styledText;
	}

    protected void handleVerifyEvent(VerifyEvent e) {
    	getRuntime().sendInput(e.text);
		e.doit = false;    	
    }
    
    private ForgeRuntime getRuntime() {
		return runtime;    	
    }
    
    public void stopConsole() {
    	Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
		    	if (forgeDocument != null) {
		    		forgeDocument.reset();
		    	}
			}   		
    	});
    }
    
    public void startConsole() {
    	Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				forgeDocument.connect(getRuntime());
		    	setDocument(forgeDocument);
			}   		
    	});
    }
    
}
