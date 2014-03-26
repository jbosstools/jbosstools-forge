package org.jboss.tools.aesh.ui.internal.document;

import org.junit.Assert;
import org.junit.Test;

public class DocumentImplTest {
	
	private DocumentImpl documentImpl = new DocumentImpl();
	
	@Test
	public void testConstructor() {
		Assert.assertNotNull(documentImpl.delegateDocument);
		Assert.assertNotNull(documentImpl.currentStyle);
		Assert.assertEquals(0, documentImpl.cursorOffset);
		Assert.assertEquals(0, documentImpl.savedCursor);
		Assert.assertNull(documentImpl.cursorListener);
	}
	
	@Test
	public void testGetCursorOffset() {
		Assert.assertEquals(0, documentImpl.getCursorOffset());
		documentImpl.cursorOffset = 7;
		Assert.assertEquals(7, documentImpl.getCursorOffset());
		
	}
	
}
