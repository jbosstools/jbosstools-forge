/**
 * Copyright (c) Red Hat, Inc., contributors and others 2013 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
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
