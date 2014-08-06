/**
 * Copyright (c) Red Hat, Inc., contributors and others 2004 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.aesh.core.test.util;

import org.jboss.tools.aesh.core.document.Document;
import org.jboss.tools.aesh.core.document.Style;

public class TestDocument implements Document {

	@Override
	public int getCursorOffset() {
		return 0;
	}

	@Override
	public int getLineOfOffset(int offset) {
		return 0;
	}

	@Override
	public int getLineOffset(int line) {
		return 0;
	}

	@Override
	public int getLineLength(int line) {
		return 0;
	}

	@Override
	public int getLength() {
		return 0;
	}

	@Override
	public void moveCursorTo(int offset) {
	}

	@Override
	public void restoreCursor() {
	}

	@Override
	public void saveCursor() {
	}

	@Override
	public void reset() {
	}

	@Override
	public void replace(int cursorOffset, int length, String str) {
	}

	@Override
	public Style newStyleFromCurrent() {
		return null;
	}

	@Override
	public void setCurrentStyle(Style style) {
	}

	@Override
	public Style getCurrentStyle() {
		return null;
	}

	@Override
	public void setDefaultStyle() {
	}

}
