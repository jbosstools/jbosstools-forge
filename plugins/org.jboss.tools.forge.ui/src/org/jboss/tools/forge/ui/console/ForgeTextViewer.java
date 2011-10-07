package org.jboss.tools.forge.ui.console;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ST;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.jboss.tools.forge.core.io.ForgeAnsiCommandFilter;
import org.jboss.tools.forge.core.io.ForgeHiddenOutputFilter;
import org.jboss.tools.forge.core.io.ForgeOutputListener;
import org.jboss.tools.forge.core.process.ForgeRuntime;
import org.jboss.tools.forge.ui.ForgeUIPlugin;

public class ForgeTextViewer extends TextViewer {

	private static String PREV_CHAR = new Character((char)2).toString();
	private static String CTRL_C = new Character((char)3).toString();
	private static String CTRL_D = new Character((char)4).toString();
	private static String NEXT_CHAR = new Character((char)6).toString();
	private static String DELETE_PREV_CHAR = new Character((char)8).toString();
	private static String PREV_HISTORY = new Character((char)16).toString();
	private static String NEXT_HISTORY = new Character((char)14).toString();
	private static String DELETE_NEXT_CHAR = new Character((char)127).toString();
	
	private class RuntimeStopListener implements PropertyChangeListener {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (ForgeRuntime.PROPERTY_STATE.equals(evt.getPropertyName()) && 
					ForgeRuntime.STATE_NOT_RUNNING.equals(evt.getNewValue())) {
				handleRuntimeStopped();
			}
		}   	
    }
	
	private class DocumentListener implements IDocumentListener {
    	@Override
        public void documentAboutToBeChanged(DocumentEvent event) {
        }
        @Override
        public void documentChanged(final DocumentEvent event) {
        	Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
		            StyledText textWidget = getTextWidget();
		            if (textWidget != null && !textWidget.isDisposed()) {
		                int lineCount = textWidget.getLineCount();
		                textWidget.setTopIndex(lineCount - 1);
		            }
				}       		
        	});
        }
    }
	
    private RuntimeStopListener stopListener;
    private ForgeOutputListener outputListener;
    private ForgeRuntime runtime;
    private Document document;
    private StyleRange currentStyleRange = null;     
    private int caretOffset = 0;
    
    public ForgeTextViewer(Composite parent, ForgeRuntime runtime) {
    	super(parent, SWT.WRAP | SWT.V_SCROLL | SWT.H_SCROLL);
    	this.runtime = runtime;
    	initialize();
    }

    private void initialize() {
        initDocument();
        initViewer();
        initCommandRecorder();
        initOutputListener();
        initStopListener();
    }
        
    private void initDocument() {
    	document = new Document();
        document.addDocumentListener(new DocumentListener());
    	setDocument(document);
    }
    
    private void initViewer() {
    	getTextWidget().setFont(JFaceResources.getFont(JFaceResources.TEXT_FONT));
    	getTextWidget().addVerifyKeyListener(new VerifyKeyListener() {			
			@Override
			public void verifyKey(VerifyEvent event) {
				if ((event.stateMask & SWT.CTRL) == SWT.CTRL ) {
					if (event.keyCode == 'd') {
						runtime.sendInput(CTRL_D);
					} else if (event.keyCode == 'c') {
						runtime.sendInput(CTRL_C);
					}
				}
			}
		});
    }
    
	protected StyledText createTextWidget(Composite parent, int styles) {
		StyledText styledText= new StyledText(parent, styles) {
			public void invokeAction(int action) {
				switch (action) {
					case ST.LINE_UP:
						runtime.sendInput(PREV_HISTORY);
						break;
					case ST.LINE_DOWN:
						runtime.sendInput(NEXT_HISTORY);
						break;
					case ST.COLUMN_PREVIOUS:
						runtime.sendInput(PREV_CHAR);
						break;
					case ST.COLUMN_NEXT:
						runtime.sendInput(NEXT_CHAR);
						break;
					case ST.DELETE_PREVIOUS:
						runtime.sendInput(DELETE_PREV_CHAR);
						break;
					case ST.DELETE_NEXT:
						runtime.sendInput(DELETE_NEXT_CHAR);
						break;
					default: super.invokeAction(action);
				}
			}
		};
		styledText.setLeftMargin(Math.max(styledText.getLeftMargin(), 2));
		return styledText;
	}

    private Color getColor(int colorCode) {
    	return Display.getDefault().getSystemColor(colorCode);
    }
    
    private void initCommandRecorder() {
    	getDocument().addDocumentListener(new CommandRecorder());
    }
    
    private void initOutputListener() {
    	ForgeOutputListener target = new ForgeOutputListener() {			
			@Override
			public void outputAvailable(final String output) {
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						try {
							String filteredOutput = output.replaceAll("\r", "");
							document.replace(caretOffset, document.getLength() - caretOffset, filteredOutput);
							getTextWidget().setCaretOffset(caretOffset = caretOffset + filteredOutput.length());
							if (currentStyleRange != null) {
								currentStyleRange.length = currentStyleRange.length + filteredOutput.length();
								getTextWidget().setStyleRange(currentStyleRange);
							}
						} catch (BadLocationException e) {
				        	ForgeUIPlugin.log(e);							
						}
					}					
				});
			}
		};
		ForgeAnsiCommandFilter ansiCommandFilter = new ForgeAnsiCommandFilter(target) {			
			@Override
			public void ansiCommandAvailable(String command) {
				executeAnsiCommand(command);
			}
		};
		outputListener = new ForgeHiddenOutputFilter(ansiCommandFilter) {
			@Override
			public void handleFilteredString(String str) {
				if (str.startsWith("Intercepted Command: ")) {
					
				} else if (str.startsWith("Executed Command: ")) {
					
				} else {
					System.out.println("unhandled hidden output: " + str);
				}
			}
		};
		runtime.addOutputListener(outputListener);
    }
    
    private void initStopListener() {
    	stopListener = new RuntimeStopListener();
    	runtime.addPropertyChangeListener(stopListener);
    }
    
    private void handleRuntimeStopped() {
    	runtime.removePropertyChangeListener(stopListener);
    	stopListener = null;
    	runtime.removeOutputListener(outputListener);
    	outputListener = null;
    }
    
    protected void handleVerifyEvent(VerifyEvent e) {
		runtime.sendInput(e.text);
		e.doit = false;    	
    }
    
    private void executeAnsiCommand(final String command) {
    	Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
		    	char c = command.charAt(command.length() - 1);
		    	switch (c) {
		    		case 'G' : moveCursorAbsoluteInLine(command); break;
		    		case 'K' : clearCurrentLine(command); break;
		    		case 'm' : changeColor(command); break;
		    		case 'H' : setCursorPosition(command); break;
		    		case 'J' : clearCurrentScreenPage(command); break;
		    		default : ForgeUIPlugin.log(new RuntimeException("Unhandled Ansi control sequence in ForgeTextViewer: "+ command));
		    	}
			}   		
    	});
    }
    
    private void moveCursorAbsoluteInLine(final String command) {
    	try {
    		int column = Integer.valueOf(command.substring(2, command.length() - 1));
    		int lineStart = document.getLineOffset(document.getLineOfOffset(caretOffset));
    		getTextWidget().setCaretOffset(caretOffset = lineStart + column - 1); 
    	} catch (BadLocationException e) {
    		ForgeUIPlugin.log(e);
    	}				
    }
    
    private void clearCurrentLine(String command) {
    	try {
        	document.replace(caretOffset, document.getLength() - caretOffset, "");
        } catch (BadLocationException e) {
        	ForgeUIPlugin.log(e);
        }
    }
    
    private void changeColor(String command) {
    	String str = command.substring(2, command.length() - 1);
    	Color newColor = null;
    	if ("30".equals(str)) {
    		newColor = getColor(SWT.COLOR_BLACK);
    	} else if ("31".equals(str)) {
    		newColor = getColor(SWT.COLOR_RED);
    	} else if ("32".equals(str)) {
    		newColor = getColor(SWT.COLOR_GREEN);
    	} else if ("33".equals(str)) {
    		newColor = getColor(SWT.COLOR_DARK_YELLOW);
    	} else if ("34".equals(str)) {
    		newColor = getColor(SWT.COLOR_BLUE);
    	} else if ("35".equals(str)) {
    		newColor = getColor(SWT.COLOR_MAGENTA);
    	} else if ("36".equals(str)) {
    		newColor = getColor(SWT.COLOR_CYAN);
    	} else if ("37".equals(str)) {
    		newColor = getColor(SWT.COLOR_GRAY);
    	}
    	if (newColor != null) {
    		currentStyleRange = new StyleRange(getTextWidget().getCharCount(), 0, newColor, null);
    	} else {
    		currentStyleRange = null;
    	}
    }
    
    private void setCursorPosition(String command) {
    	String str = command.substring(2, command.length() - 1);
    	int i = str.indexOf(';');
    	int line = 0, column = 0;
    	if (i != -1) {
    		line = Integer.valueOf(str.substring(0, i));
    		column = Integer.valueOf(str.substring(i + 1));
    	} else if (str.length() > 0) {
    		line = Integer.valueOf(str);
    	}
    	StyledText textWidget = getTextWidget();
    	int offset = textWidget.getOffsetAtLine(line);
    	int maxColumn = textWidget.getLine(line).length();
    	offset += Math.min(maxColumn, column);
    	getTextWidget().setCaretOffset(caretOffset = offset);
    }
    
    private void clearCurrentScreenPage(String command) {
    	String str = command.substring(2, command.length() - 1);
    	if ("2".equals(str)) {
    		getDocument().set("");
    	}
    }
    
}
