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

public class RestoreCursorPositionTest {
	
	private boolean cursorRestored = false;
	
	private Document testDocument = new TestDocument() {
		@Override public void restoreCursor() { cursorRestored = true; }
	};
	
	@Test
	public void testGetType() {
		RestoreCursorPosition restoreCursorPosition = new RestoreCursorPosition(null);
		Assert.assertEquals(
				CommandType.RESTORE_CURSOR_POSITION, 
				restoreCursorPosition.getType());
	}
	
	@Test
	public void testHandle() {
		RestoreCursorPosition restoreCursorPosition = new RestoreCursorPosition(null);
		Assert.assertFalse(cursorRestored);
		restoreCursorPosition.handle(testDocument);
		Assert.assertTrue(cursorRestored);
	}

}
