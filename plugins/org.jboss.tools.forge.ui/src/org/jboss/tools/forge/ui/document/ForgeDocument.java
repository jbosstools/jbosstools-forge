package org.jboss.tools.forge.ui.document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.jboss.tools.forge.core.io.ForgeAnsiCommandFilter;
import org.jboss.tools.forge.core.io.ForgeOutputListener;
import org.jboss.tools.forge.core.process.ForgeRuntime;
import org.jboss.tools.forge.ui.ForgeUIPlugin;
import org.jboss.tools.forge.ui.console.ForgeCommandFilter;

public class ForgeDocument extends Document {
	
	public static final ForgeDocument INSTANCE = new ForgeDocument();
	
	public interface CursorListener {
		void cursorMoved();
	}
	
	private class SyncForgeCommandFilter extends ForgeCommandFilter {
		public SyncForgeCommandFilter(ForgeOutputListener listener) {
			super(listener);
		}
		public void outputAvailable(final String output) {
			Display.getDefault().syncExec(new Runnable() {
				@Override
				public void run() {
					SyncForgeCommandFilter.super.outputAvailable(output);
				}				
			});
		}		
	}
		
	private ForgeRuntime runtime;
	private int cursorOffset = 0;
	private StyleRange currentStyleRange;
	private ForgeOutputListener outputListener;
	private Map<String, Color> colors;
	private List<StyleRange> styleRanges = new ArrayList<StyleRange>();
	private Set<CursorListener> cursorListeners = new HashSet<CursorListener>();
	
	private ForgeDocument() {
		initColors();
	}
	
	public void finalize() throws Throwable {
		disposeColors();
		super.finalize();
	}
	
	public void connect(ForgeRuntime runtime) {
		disconnect();
		this.runtime = runtime;
		ForgeOutputListener target = new ForgeOutputListener() {		
			@Override
			public void outputAvailable(String output) {
				handleAvailableOutput(output);
			}
		};
		ForgeAnsiCommandFilter facf = new ForgeAnsiCommandFilter(target) {			
			@Override
			public void ansiCommandAvailable(String command) {
				executeAnsiCommand(command);
			}
		};
		outputListener = new SyncForgeCommandFilter(facf);
		runtime.addOutputListener(outputListener);
	}
	
	private void disconnect() {
		if (runtime != null) {
			runtime.removeOutputListener(outputListener);
			runtime = null;
		}
		reset();
	}
	
	private void executeAnsiCommand(final String command) {
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
	
	private void handleAvailableOutput(String output) {
		try {
			String filteredOutput = output.replaceAll("\r", "");
			if (currentStyleRange != null) {
				currentStyleRange.length = currentStyleRange.length + filteredOutput.length();
			}
			replace(cursorOffset, getLength() - cursorOffset, filteredOutput);
			moveCursorTo(cursorOffset + filteredOutput.length());
		} catch (BadLocationException e) {
        	ForgeUIPlugin.log(e);							
		}
	}

    private void moveCursorAbsoluteInLine(final String command) {
    	try {
    		int column = Integer.valueOf(command.substring(2, command.length() - 1));
    		int lineStart = getLineOffset(getLineOfOffset(cursorOffset));
    		moveCursorTo(lineStart + column - 1); 
    	} catch (BadLocationException e) {
    		ForgeUIPlugin.log(e);
    	}				
    }
    
    private void clearCurrentLine(String command) {
    	try {
        	replace(cursorOffset, getLength() - cursorOffset, "");
        } catch (BadLocationException e) {
        	ForgeUIPlugin.log(e);
        }
    }
    
    private void changeColor(String command) {
    	String str = command.substring(2, command.length() - 1);
    	Color newColor = colors.get(str);
    	if (newColor != null) {
    		currentStyleRange = new StyleRange(getLength(), 0, newColor, null);
    		styleRanges.add(currentStyleRange);
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
    	try {
    		int offset = getLineOffset(line);
    		int maxColumn = getLineLength(line);
    		offset += Math.min(maxColumn, column);
    		moveCursorTo(offset);
    	} catch (BadLocationException e) {
    		ForgeUIPlugin.log(e);
    	}
    }
    
    private void clearCurrentScreenPage(String command) {
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
    
    private void initColors() {
    	colors = new HashMap<String, Color>();
    	colors.put("30", new Color (Display.getDefault(), 0,0,0));
    	colors.put("31", new Color (Display.getDefault(), 0xFF,0,0));
    	colors.put("32", new Color (Display.getDefault(), 0,0xFF,0));
    	colors.put("33", new Color (Display.getDefault(), 0x80,0x80,0));
    	colors.put("34", new Color (Display.getDefault(), 0,0,0xFF));
    	colors.put("35", new Color (Display.getDefault(), 0xFF,0,0xFF));
    	colors.put("36", new Color (Display.getDefault(), 0,0xFF,0xFF));
    	colors.put("37", new Color (Display.getDefault(), 0xC0,0xC0,0xC0));
    }
    
    private void disposeColors() {
    	for (Color color : colors.values()) {
    		color.dispose();
    	}
    	colors = null;
    }

	public StyleRange getCurrentStyleRange() {
		return currentStyleRange;
	}
	
	public StyleRange[] getStyleRanges() {
		return styleRanges.toArray(new StyleRange[styleRanges.size()]);
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
	
	private void moveCursorTo(int newOffset) {
		cursorOffset = newOffset;
		for (CursorListener listener : cursorListeners) {
			listener.cursorMoved();
		}
	}
    
}
