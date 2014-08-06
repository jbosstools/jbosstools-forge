/**
 * Copyright (c) Red Hat, Inc., contributors and others 2004 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.aesh.core.internal.ansi;

import org.jboss.tools.aesh.core.document.Document;
import org.jboss.tools.aesh.core.test.util.TestDocument;
import org.junit.Assert;
import org.junit.Test;

public class CursorHorizontalAbsoluteTest {
	
	private int testOffset = 0;
	
	private Document testDocument = new TestDocument() {
		@Override public int getLineOffset(int line) { return line * 80; }
		@Override public int getLineOfOffset(int offset) { return offset / 80; }
		@Override public int getCursorOffset() { return testOffset; }
		@Override public void moveCursorTo(int offset) { testOffset = offset; }
	};
	
	@Test
	public void testGetType() {
		CursorHorizontalAbsolute cursorHorizontalAbsolute = new CursorHorizontalAbsolute("17");
		Assert.assertEquals(CommandType.CURSOR_HORIZONTAL_ABSOLUTE, cursorHorizontalAbsolute.getType());
	}
	
	@Test
	public void testHandle() {
		CursorHorizontalAbsolute cursorHorizontalAbsolute = new CursorHorizontalAbsolute("17");
		testOffset = 833;
		cursorHorizontalAbsolute.handle(testDocument);
		Assert.assertEquals(817, testOffset);
	}

}
