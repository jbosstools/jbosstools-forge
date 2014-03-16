package org.jboss.tools.aesh.ui.document;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.text.Document;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.TextStyle;
import org.jboss.tools.aesh.ui.internal.FontManager;

public class DelegateDocument extends Document {
	
	public interface CursorListener {
		void cursorMoved();
	}
	
	private int cursorOffset = 0;
	private Set<CursorListener> cursorListeners = new HashSet<CursorListener>();
	private DelegateStyleRange currentStyleRange;
	private DelegatingDocument proxy;
	
	private int savedCursor = 0;
	
	public DelegateDocument() {
		proxy = new DelegatingDocument(this);
		currentStyleRange = getDefaultStyleRange();
	}
	
	void saveCursor() {
		savedCursor = getCursorOffset();
	}
	
	void restoreCursor() {
		moveCursorTo(savedCursor);
	}
	
    public void reset() {
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

	public int getCursorOffset() {
		return cursorOffset;
	}
	
	public void addCursorListener(CursorListener listener) {
		cursorListeners.add(listener);
	}
	
	public void removeCursorListener(CursorListener listener) {
		cursorListeners.remove(listener);
	}
	
	public DelegateStyleRange getCurrentStyleRange() {
		return currentStyleRange;
	}
	
	public void setCurrentStyleRange(DelegateStyleRange styleRange) {
		currentStyleRange = styleRange;
	}
	
	public DelegatingDocument getProxy() {
		return proxy;
	}
	
	DelegateStyleRange getDefaultStyleRange() {
		Font font = FontManager.INSTANCE.getDefault();
		Color foreground = ColorConstants.BLACK;
		Color background = ColorConstants.WHITE;		
		return new DelegateStyleRange(new StyleRange(new TextStyle(font, foreground, background)));
	}
	
}
