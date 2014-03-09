package org.jboss.tools.aesh.ui.document;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.swt.widgets.Display;
import org.jboss.tools.aesh.core.document.Document;
import org.jboss.tools.aesh.core.document.Style;
import org.jboss.tools.aesh.ui.AeshUIPlugin;

public class DelegatingDocument implements Document {
	
	private DelegateDocument document;
	private Style currentStyleRange;
	
	public DelegatingDocument(DelegateDocument document) {
		this.document = document;
	}

	@Override
	public int getCursorOffset() {
		return document.getCursorOffset();
	}
	
	@Override
	public int getLineOfOffset(int offset) {
		int result = -1;
		try {
			result = document.getLineOfOffset(offset);
		} catch (BadLocationException e) {
			AeshUIPlugin.log(e);
		}
		return result;
	}
	
	@Override
	public int getLineOffset(int line) {
		int result = -1;
		try {
			result = document.getLineOffset(line);
		} catch (BadLocationException e) {
			AeshUIPlugin.log(e);
		}
		return result;
	}
	
	@Override 
	public int getLineLength(int line) {
		int result = -1;
		try {
			result = document.getLineLength(line);
		} catch (BadLocationException e) {
			AeshUIPlugin.log(e);
		}
		return result;
	}

	@Override
	public void moveCursorTo(final int offset) {
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				document.moveCursorTo(offset);
			}				
		});
	}
	
	@Override
	public void reset() {
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				document.reset();
			}				
		});
		document.reset();
	}

	@Override
	public int getLength() {
		return document.getLength();
	}

	@Override
	public void replace(final int pos, final int length, final String text) {
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
			  	try {
		        	document.replace(pos, length, text);
		        } catch (BadLocationException e) {
		        	AeshUIPlugin.log(e);
		        }
			}				
		});
	}

	@Override
	public void restoreCursor() {
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				document.restoreCursor();
			}				
		});
	}

	@Override
	public void saveCursor() {
		document.saveCursor();
	}

	@Override
	public Style newStyleFromCurrent() {
		DelegateStyleRange oldStyleRange = document.getCurrentStyleRange();
		DelegateStyleRange newStyleRange = new DelegateStyleRange(oldStyleRange);
		newStyleRange.start = oldStyleRange.start + oldStyleRange.length;
		newStyleRange.length = 0;
		return new DelegatingStyleRange(newStyleRange);
	}

	@Override
	public void setCurrentStyle(Style styleRangeProxy) {
		if (styleRangeProxy instanceof DelegatingStyleRange) {
			DelegateStyleRange styleRange = ((DelegatingStyleRange)styleRangeProxy).getStyleRange();
			document.setCurrentStyleRange(styleRange);
			currentStyleRange = styleRangeProxy;
		}
	}
	
	@Override
	public Style getCurrentStyle() {
		return currentStyleRange;
	}
	
	@Override
	public void setDefaultStyle() {
		DelegateStyleRange styleRange = document.getDefaultStyleRange();
		styleRange.start = document.getLength();
		styleRange.length = 0;
		setCurrentStyle(new DelegatingStyleRange(styleRange));
	}

}
