package org.jboss.tools.aesh.ui.document;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.widgets.Display;
import org.jboss.tools.aesh.core.ansi.ControlSequence;
import org.jboss.tools.aesh.core.ansi.ControlSequenceFilter;
import org.jboss.tools.aesh.core.console.AeshConsole;
import org.jboss.tools.aesh.core.io.AeshOutputStream.StreamListener;
import org.jboss.tools.aesh.ui.AeshUIPlugin;

public class AeshDocument extends Document {
	
	public interface CursorListener {
		void cursorMoved();
	}
	
	private StreamListener stdOutListener, stdErrListener;
	private ControlSequenceFilter ansiCommandSequenceFilter;
	private int cursorOffset = 0;
	private AeshConsole console;
	private Set<CursorListener> cursorListeners = new HashSet<CursorListener>();
	private List<StyleRange> styleRanges = new ArrayList<StyleRange>();
	private StyleRange currentStyleRange;
	
	public AeshDocument() {
		stdOutListener = new StreamListener() {			
			@Override
			public void outputAvailable(final String output) {
				Display.getDefault().syncExec(new Runnable() {
					@Override
					public void run() {
						handleOutputAvailable(output);
					}				
				});
			}
		};
		ansiCommandSequenceFilter = new ControlSequenceFilter(stdOutListener) {			
			@Override
			public void controlSequenceAvailable(final ControlSequence controlSequence) {
				Display.getDefault().syncExec(new Runnable() {
					@Override
					public void run() {
						handleControlSequence(controlSequence);
					}					
				});
			}
		};
		stdErrListener = new StreamListener() {		
			@Override
			public void outputAvailable(final String output) {
				Display.getDefault().syncExec(new Runnable() {
					@Override
					public void run() {
						handleOutputAvailable(output);
					}				
				});
			}
		};
	}
	
	private void handleControlSequence(ControlSequence controlSequence) {
		System.out.println("handleControlSequence(" + controlSequence.getControlSequenceString() + ")");
    	switch (controlSequence.getType()) {
    		case CURSOR_UP: break;
    		case CURSOR_DOWN: break;
    		case CURSOR_FORWARD: break;
    		case CURSOR_BACK: handleCursorBack(controlSequence); break;
    		case CURSOR_NEXT_LINE: break;
    		case CURSOR_PREVIOUS_LINE: break;
    		case CURSOR_HORIZONTAL_ABSOLUTE: handleCursorHorizontalAbsolute(controlSequence); break;
    		case CURSOR_POSITION: handleCursorPosition(controlSequence); break;
    		case ERASE_DATA: handleEraseData(controlSequence); break;
    		case ERASE_IN_LINE: handleEraseInLine(controlSequence); break;
    		case SCROLL_UP: break;
    		case SCROLL_DOWN: break;
    		case HORIZONTAL_AND_VERTICAL_POSITION: break;
    		case SELECT_GRAPHIC_RENDITION: handleSelectGraphicRendition(controlSequence); break;
    		case DEVICE_STATUS_REPORT: break;
    		case SAVE_CURSOR_POSITION: break;
    		case RESTORE_CURSOR_POSITION: break;
    		case HIDE_CURSOR: break;
    		case SHOW_CURSOR: break;
    		default : 
    			AeshUIPlugin.log(new RuntimeException(
    				"Unhandled Ansi control sequence in ForgeTextViewer: " + 
    		        controlSequence.getControlSequenceString()));
    	}
	}
	
	private void handleCursorBack(ControlSequence controlSequence) {
		String command = controlSequence.getControlSequenceString();
		int amount = Integer.valueOf(command.substring(2, command.length() - 1));
		int current = getCursorOffset();
		moveCursorTo(current - amount);
	}
	
    private void handleCursorHorizontalAbsolute(ControlSequence controlSequence) {
    	try {
    		String command = controlSequence.getControlSequenceString();
    		int column = Integer.valueOf(command.substring(2, command.length() - 1));
    		int lineStart = getLineOffset(getLineOfOffset(cursorOffset));
    		moveCursorTo(lineStart + column); 
    	} catch (BadLocationException e) {
    		AeshUIPlugin.log(e);
    	}				
    }
    
    private void handleEraseInLine(ControlSequence controlSequence) {
    	try {
        	replace(cursorOffset, getLength() - cursorOffset, "");
        } catch (BadLocationException e) {
        	AeshUIPlugin.log(e);
        }
    }
    
    private void handleSelectGraphicRendition(ControlSequence controlSequence) {
    	System.out.println(controlSequence.getControlSequenceString());
//    	String[] args = command.substring(0, command.length() - 1).split(";");
//    	Color foreground = AeshColor.fromCode(Integer.valueOf(args[1])).getColor();
//    	Color background = AeshColor.fromCode(Integer.valueOf(args[2])).getColor();
//		currentStyleRange = new StyleRange(getLength(), 0, foreground, background);
//		styleRanges.add(currentStyleRange);
     }
    
    private void handleCursorPosition(ControlSequence controlSequence) {
    	String command = controlSequence.getControlSequenceString();
    	String str = command.substring(2, command.length() - 1);
    	int i = str.indexOf(';');
    	int line = 0, column = 0;
    	if (i != -1) {
    		line = Integer.valueOf(str.substring(0, i));
    		column = Integer.valueOf(str.substring(i + 1));
    	} else if (str.length() > 0) {
    		line = Integer.valueOf(str);
    	}
    	try {
    		int offset = getLineOffset(line);
    		int maxColumn = getLineLength(line);
    		offset += Math.min(maxColumn, column);
    		moveCursorTo(offset);
    	} catch (BadLocationException e) {
    		AeshUIPlugin.log(e);
    	}
    }
    
    private void handleEraseData(ControlSequence controlSequence) {
    	String command = controlSequence.getControlSequenceString();
    	String str = command.substring(2, command.length() - 1);
    	if ("2".equals(str)) {
    		reset();
    	}
    }
    
    private void reset() {
		set("");
		moveCursorTo(0);
		styleRanges.clear();
		currentStyleRange = null;
    }
    
	private void moveCursorTo(int newOffset) {
		cursorOffset = newOffset;
		for (CursorListener listener : cursorListeners) {
			listener.cursorMoved();
		}
	}

	private void handleOutputAvailable(String output) {
		System.out.println("handleOutputAvailable(" + output + ")");
		try {
			output.replaceAll("\r", "");
			if (currentStyleRange != null) {
				currentStyleRange.length += output.length();
			}
			replace(cursorOffset, getLength() - cursorOffset, output);
			moveCursorTo(cursorOffset + output.length());
		} catch (BadLocationException e) {
        	e.printStackTrace();							
		}
	}

	public void connect(AeshConsole aeshConsole) {
		if (console == null) {
			console = aeshConsole;
			console.addStdOutListener(ansiCommandSequenceFilter);
			console.addStdErrListener(stdErrListener);
		}
	}
	
	public void disconnect() {
		if (console != null) {
			console.removeStdOutListener(ansiCommandSequenceFilter);
			console.removeStdErrListener(stdErrListener);
			console = null;
		}
	}

	public int getCursorOffset() {
		return cursorOffset;
	}
	
	public void addCursorListener(CursorListener listener) {
		cursorListeners.add(listener);
	}
	
	public void removeCursorListener(CursorListener listener) {
		cursorListeners.remove(listener);
	}
	
	public StyleRange getCurrentStyleRange() {
		return currentStyleRange;
	}
	
}
