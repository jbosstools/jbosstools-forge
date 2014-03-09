package org.jboss.tools.aesh.core.document;


public interface Document {
	
	int getCursorOffset();
	int getLineOfOffset(int offset);
	int getLineOffset(int line);
	int getLineLength(int line);
	int getLength();
	
	void moveCursorTo(int offset);
	void restoreCursor();
	void saveCursor();
	void reset();
	void replace(int cursorOffset, int length, String str);
	
	Style newStyleFromCurrent();
	void setCurrentStyle(Style style);
	Style getCurrentStyle();
	void setDefaultStyle();

}
