/**
 * Copyright (c) Red Hat, Inc., contributors and others 2004 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.aesh.ui.internal.viewer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.jboss.tools.aesh.core.document.Document;
import org.jboss.tools.aesh.ui.test.util.TestDocument;
import org.junit.Assert;
import org.junit.Test;

public class CursorListenerImplTest {
	
	private int cursorOffset = 0;
	private int caretOffset = 0;
	
	private TextWidget testTextWidget = new TextWidget(new Shell(), SWT.NONE) {
		@Override public void setCaretOffset(int offset) { caretOffset = offset; }
	};
	private Document testDocument = new TestDocument() {
		@Override public int getCursorOffset() { return cursorOffset; }
	};
	
	private CursorListenerImpl testCursorListenerImpl = new CursorListenerImpl(testTextWidget, testDocument);
	
	@Test
	public void testConstructor() {
		Assert.assertEquals(testTextWidget, testCursorListenerImpl.textWidget);
		Assert.assertEquals(testDocument, testCursorListenerImpl.document);
	}
	
	@Test
	public void testCursorMoved() {
		cursorOffset = 99;
		testCursorListenerImpl.cursorMoved();
		Assert.assertEquals(99, caretOffset);
		testTextWidget.dispose();
		cursorOffset = 66;
		caretOffset = 33;
		testCursorListenerImpl.cursorMoved();
		Assert.assertEquals(33, caretOffset);
		testCursorListenerImpl.textWidget = null;
		cursorOffset = 55;
		caretOffset = 11;
		testCursorListenerImpl.cursorMoved();
		Assert.assertEquals(11, caretOffset);
	}

}
