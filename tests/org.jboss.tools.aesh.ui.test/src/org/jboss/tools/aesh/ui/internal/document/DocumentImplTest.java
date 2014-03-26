package org.jboss.tools.aesh.ui.internal.document;

import org.junit.Assert;
import org.junit.Test;

public class DocumentImplTest {
	
	private DocumentImpl documentImpl = new DocumentImpl();
	
	@Test
	public void testConstructor() {
		Assert.assertNotNull(documentImpl.document);
		Assert.assertNotNull(documentImpl.currentStyle);
		Assert.assertEquals(0, documentImpl.cursorOffset);
		Assert.assertEquals(0, documentImpl.savedCursor);
		Assert.assertNull(documentImpl.cursorListener);
	}
	
}
