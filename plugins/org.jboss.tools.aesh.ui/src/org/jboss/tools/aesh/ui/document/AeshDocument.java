package org.jboss.tools.aesh.ui.document;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.widgets.Display;
import org.jboss.tools.aesh.core.ansi.ControlSequence;
import org.jboss.tools.aesh.core.ansi.ControlSequenceFilter;
import org.jboss.tools.aesh.core.console.AeshConsole;
import org.jboss.tools.aesh.core.document.DocumentProxy;
import org.jboss.tools.aesh.core.io.AeshOutputStream.StreamListener;
import org.jboss.tools.aesh.ui.AeshUIConstants;

public class AeshDocument extends Document {
	
	public interface CursorListener {
		void cursorMoved();
	}
	
	private StreamListener stdOutListener, stdErrListener;
	private ControlSequenceFilter ansiCommandSequenceFilter;
	private int cursorOffset = 0;
	private AeshConsole console;
	private Set<CursorListener> cursorListeners = new HashSet<CursorListener>();
	private StyleRange currentStyleRange;
	private DocumentProxy proxy;
	
	private int savedCursor = 0;
	
	public AeshDocument() {
		proxy = new AeshDocumentProxy(this);
		currentStyleRange = getDefaultStyleRange();
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
						controlSequence.handle(proxy);
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
	
	void saveCursor() {
		savedCursor = getCursorOffset();
	}
	
	void restoreCursor() {
		moveCursorTo(savedCursor);
	}
	
    void reset() {
		set("");
		moveCursorTo(0);
		currentStyleRange = getDefaultStyleRange();
    }
    
	void moveCursorTo(int newOffset) {
		cursorOffset = newOffset;
		for (CursorListener listener : cursorListeners) {
			listener.cursorMoved();
		}
	}

	private void handleOutputAvailable(String output) {
		try {
			output.replaceAll("\r", "");
			replace(getCursorOffset(), getLength() - getCursorOffset(), output);
			moveCursorTo(getCursorOffset() + output.length());
		} catch (BadLocationException e) {
        	e.printStackTrace();							
		}
	}
	
	@Override
	public void replace(int pos, int length, String text) throws BadLocationException {
		if (currentStyleRange != null) {
			int increase = getCursorOffset() -getLength() + text.length();
			currentStyleRange.length += increase;
		}
		super.replace(pos, length, text);
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
	
	public void setCurrentStyleRange(StyleRange styleRange) {
		currentStyleRange = styleRange;
	}
	
	private StyleRange getDefaultStyleRange() {
		Font font = JFaceResources.getFont(AeshUIConstants.AESH_CONSOLE_FONT);
		Color foreground = AeshColor.BLACK_TEXT.getColor();
		Color background = AeshColor.WHITE_BG.getColor();		
		return new StyleRange(new TextStyle(font, foreground, background));
	}
	
}
