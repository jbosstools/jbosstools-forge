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
import org.jboss.tools.aesh.core.document.DocumentProxy;
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
	
	private int savedCursor = 0;
	
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
	
	private DocumentProxy  proxy = new DocumentProxy() {

		@Override
		public int getCursorOffset() {
			return AeshDocument.this.getCursorOffset();
		}
		
		@Override
		public int getLineOfOffset(int offset) {
			int result = -1;
			try {
				result = AeshDocument.this.getLineOfOffset(offset);
			} catch (BadLocationException e) {
				AeshUIPlugin.log(e);
			}
			return result;
		}
		
		@Override
		public int getLineOffset(int line) {
			int result = -1;
			try {
				result = AeshDocument.this.getLineOffset(line);
			} catch (BadLocationException e) {
				AeshUIPlugin.log(e);
			}
			return result;
		}
		
		@Override 
		public int getLineLength(int line) {
			int result = -1;
			try {
				result = AeshDocument.this.getLineLength(line);
			} catch (BadLocationException e) {
				AeshUIPlugin.log(e);
			}
			return result;
		}

		@Override
		public void moveCursorTo(int offset) {
			AeshDocument.this.moveCursorTo(offset);
		}
		
		@Override
		public void reset() {
			AeshDocument.this.reset();
		}

		@Override
		public int getLength() {
			return AeshDocument.this.getLength();
		}

		@Override
		public void replace(int pos, int length, String text) {
	    	try {
	        	AeshDocument.this.replace(pos, length, text);
	        } catch (BadLocationException e) {
	        	AeshUIPlugin.log(e);
	        }
		}

		@Override
		public void restoreCursor() {
			AeshDocument.this.restoreCursor();
		}

		@Override
		public void saveCursor() {
			AeshDocument.this.saveCursor();
		}
		
	};
	
	private void handleControlSequence(ControlSequence controlSequence) {
		System.out.println("handleControlSequence(" + controlSequence.getControlSequenceString() + ")");
		controlSequence.handle(proxy);
	}
	
	private void saveCursor() {
		savedCursor = getCursorOffset();
	}
	
	private void restoreCursor() {
		moveCursorTo(savedCursor);
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
			replace(getCursorOffset(), getLength() - getCursorOffset(), output);
			moveCursorTo(getCursorOffset() + output.length());
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
