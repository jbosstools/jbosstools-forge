/**
 * Copyright (c) Red Hat, Inc., contributors and others 2004 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.aesh.ui.internal.document;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.widgets.Display;
import org.jboss.tools.aesh.core.document.Style;
import org.jboss.tools.aesh.ui.internal.AeshUIPlugin;

public class DocumentImpl implements org.jboss.tools.aesh.core.document.Document {
	
	Document delegateDocument;
	StyleImpl currentStyle;
	CursorListener cursorListener;
	int savedCursor = 0;
	int cursorOffset = 0;
	IDocumentListener documentListener = null;
	
	public DocumentImpl() {
		this.delegateDocument = new Document();
		this.currentStyle = StyleImpl.getDefault();
	}

	@Override
	public int getCursorOffset() {
		return cursorOffset;
	}
	
	@Override
	public int getLineOfOffset(int offset) {
		int result = -1;
		try {
			result = delegateDocument.getLineOfOffset(offset);
		} catch (BadLocationException e) {
			AeshUIPlugin.log(e);
		}
		return result;
	}
	
	@Override
	public int getLineOffset(int line) {
		int result = -1;
		try {
			result = delegateDocument.getLineOffset(line);
		} catch (BadLocationException e) {
			AeshUIPlugin.log(e);
		}
		return result;
	}
	
	@Override 
	public int getLineLength(int line) {
		int result = -1;
		try {
			result = delegateDocument.getLineLength(line);
		} catch (BadLocationException e) {
			AeshUIPlugin.log(e);
		}
		return result;
	}

	@Override
	public void moveCursorTo(final int offset) {
		cursorOffset = offset;
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				cursorListener.cursorMoved();
			}				
		});
	}
	
	@Override
	public void reset() {
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				delegateDocument.set("");
			}				
		});
		moveCursorTo(0);
		setDefaultStyle();
	}

	@Override
	public int getLength() {
		return delegateDocument.getLength();
	}

	@Override
	public void replace(final int pos, final int length, final String text) {
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
			  	try {
		        	delegateDocument.replace(pos, length, text);
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
				moveCursorTo(savedCursor);
			}				
		});
	}

	@Override
	public void saveCursor() {
		savedCursor = getCursorOffset();
	}

	@Override
	public Style newStyleFromCurrent() {
		StyleRange oldStyleRange = currentStyle.getStyleRange();
		StyleRange newStyleRange = new StyleRange(oldStyleRange);
		newStyleRange.start = oldStyleRange.start + oldStyleRange.length;
		newStyleRange.length = 0;
		return new StyleImpl(newStyleRange);
	}

	@Override
	public void setCurrentStyle(Style styleRangeProxy) {
		if (styleRangeProxy instanceof StyleImpl) {
			currentStyle = (StyleImpl)styleRangeProxy;
		}
	}
	
	@Override
	public Style getCurrentStyle() {
		return currentStyle;
	}
	
	@Override
	public void setDefaultStyle() {
		StyleImpl defaultStyle = StyleImpl.getDefault();
		StyleRange styleRange = defaultStyle.getStyleRange();
		styleRange.start = delegateDocument.getLength();
		styleRange.length = 0;
		setCurrentStyle(new StyleImpl(styleRange));
	}
	
	public Document getDelegate() {
		return delegateDocument;
	}
	
	public void setCursorListener(CursorListener listener) {
		this.cursorListener = listener;
	}
	
	public void setDocumentListener(IDocumentListener listener) {
		if (listener != documentListener) {
			if (documentListener != null) {
				delegateDocument.removeDocumentListener(documentListener);
				documentListener = null;
			}
			if (listener != null) {
				delegateDocument.addDocumentListener(listener);
				documentListener = listener;
			}
		}
	}
	
}
