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

public class SaveCursorPositionTest {

	private boolean cursorSaved = false;
	
	private Document testDocument = new TestDocument() {
		@Override public void saveCursor() { cursorSaved = true; }
	};
	
	@Test
	public void testGetType() {
		SaveCursorPosition saveCursorPosition = new SaveCursorPosition(null);
		Assert.assertEquals(
				CommandType.SAVE_CURSOR_POSITION, 
				saveCursorPosition.getType());
	}
	
	@Test
	public void testHandle() {
		SaveCursorPosition saveCursorPosition = new SaveCursorPosition(null);
		Assert.assertFalse(cursorSaved);
		saveCursorPosition.handle(testDocument);
		Assert.assertTrue(cursorSaved);
	}


}
