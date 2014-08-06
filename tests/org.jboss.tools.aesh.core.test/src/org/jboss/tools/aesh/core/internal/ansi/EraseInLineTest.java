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

public class EraseInLineTest {
	
	private int cursorOffset = 35;
	private int length = 45;
	private boolean defaultStyleSet = false;
	
	private int replacedPos = 0;
	private int replacedLength = 0;
	private String replacedText = null;
	
	private Document testDocument = new TestDocument() {
		@Override public int getCursorOffset() { return cursorOffset; }
		@Override public int getLength() { return length; }
		@Override public void setDefaultStyle() { defaultStyleSet = true; }
		@Override public void replace(int pos, int length, String text) {
			replacedPos = pos;
			replacedLength = length;
			replacedText = text;
		}
	};
	
	@Test
	public void testGetType() {
		EraseInLine eraseInLine = new EraseInLine(null);
		Assert.assertEquals(CommandType.ERASE_IN_LINE, eraseInLine.getType());
	}
	
	@Test
	public void testHandle() {
		EraseInLine eraseInLine = new EraseInLine(null);
		Assert.assertEquals(0, replacedPos);
		Assert.assertEquals(0, replacedLength);
		Assert.assertNull(replacedText);
		Assert.assertFalse(defaultStyleSet);
		eraseInLine.handle(testDocument);
		Assert.assertEquals(35, replacedPos);
		Assert.assertEquals(10, replacedLength);
		Assert.assertEquals("", replacedText);
		Assert.assertTrue(defaultStyleSet);
	}

}
