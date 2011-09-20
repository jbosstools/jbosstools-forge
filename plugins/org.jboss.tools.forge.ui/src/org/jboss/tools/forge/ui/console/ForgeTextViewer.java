package org.jboss.tools.forge.ui.console;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
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

	private static String BACKSPACE = new Character('\b').toString();
	private static String UP_ARROW = new Character((char)16).toString();
	private static String DOWN_ARROW = new Character((char)14).toString();

	private class RuntimeStopListener implements PropertyChangeListener {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (ForgeRuntime.PROPERTY_STATE.equals(evt.getPropertyName()) && 
					ForgeRuntime.STATE_NOT_RUNNING.equals(evt.getNewValue())) {
				handleRuntimeStopped();
			}
		}   	
    }
    
	private class ConsoleKeyListener implements KeyListener {
		@Override
		public void keyPressed(KeyEvent e) {
			if (e.keyCode == SWT.BS) {
				handleBackspace();
			} else if (e.keyCode == SWT.ARROW_UP) {
				handleArrowUp();
			} else if (e.keyCode == SWT.ARROW_DOWN) {
				handleArrowDown();
			} else if (e.keyCode == SWT.F1) {
				handleF1Down();
			}
		}
		@Override
		public void keyReleased(KeyEvent e) {
		}		
	}
   
	private class DocumentListener implements IDocumentListener {
    	@Override
        public void documentAboutToBeChanged(DocumentEvent event) {
        }
        @Override
        public void documentChanged(DocumentEvent event) {
        	Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
		            StyledText textWidget = getTextWidget();
		            if (textWidget != null && !textWidget.isDisposed()) {
		                int lineCount = textWidget.getLineCount();
		                textWidget.setTopIndex(lineCount - 1);
		                textWidget.setCaretOffset(textWidget.getCharCount());
		            }
				}       		
        	});
        }
    }
	
    private RuntimeStopListener stopListener;
    private ForgeOutputListener outputListener;
    private ForgeRuntime runtime;
    private ForgeDocument document;
    
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
    	document = new ForgeDocument();
        document.addDocumentListener(new DocumentListener());
    	setDocument(document);
    }
    
    private void initViewer() {
    	StyledText textWidget = getTextWidget();
    	textWidget.setFont(JFaceResources.getFont(JFaceResources.TEXT_FONT));
    	textWidget.addKeyListener(new ConsoleKeyListener());
    	currentStyleRange = new StyleRange(0, 0, getColor(SWT.COLOR_BLACK), null);
    	textWidget.setStyleRange(currentStyleRange);
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
						document.appendString(output);
						if (currentStyleRange != null) {
							currentStyleRange.length = currentStyleRange.length + output.length();
							getTextWidget().setStyleRange(currentStyleRange);
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
				System.out.println("handleHiddenOutput : " + str);
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
    
    private void handleBackspace() {
    	runtime.sendInput(BACKSPACE);
    }
    
    private void handleArrowUp() {
    	runtime.sendInput(UP_ARROW);
    }
    
    private void handleArrowDown() {
    	runtime.sendInput(DOWN_ARROW);
    }
    
    private void handleF1Down() {
    	runtime.sendInput(new Character((char)31).toString() + "hidden command!\n"); 
    }
    
    private void executeAnsiCommand(final String command) {
    	Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
		    	char c = command.charAt(command.length() - 1);
		    	switch (c) {
		    		case 'G' : moveCursorAbsolute(command); break;
		    		case 'K' : clearCurrentLine(command); break;
		    		case 'm' : changeColor(command); break;
		    		default : ForgeUIPlugin.log(new RuntimeException("Unhandled Ansi control sequence in ForgeTextViewer: "+ command));
		    	}
			}   		
    	});
    }
    
    private void moveCursorAbsolute(final String command) {
    	try {
    		int column = Integer.valueOf(command.substring(2, command.length() - 1));
    		int lineStart = document.getLineOffset(document.getLineOfOffset(getTextWidget().getCaretOffset()));
    		getTextWidget().setCaretOffset(lineStart + column - 1); 
    	} catch (BadLocationException e) {
    		ForgeUIPlugin.log(e);
    	}				
    }
    
    private void clearCurrentLine(String command) {
    	try {
        	int caretOffset = getTextWidget().getCaretOffset();
        	document.replace(caretOffset, document.getLength() - caretOffset, "");
        } catch (BadLocationException e) {
        	ForgeUIPlugin.log(e);
        }
    }
    
    private StyleRange currentStyleRange = null; 
    
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
    
}
