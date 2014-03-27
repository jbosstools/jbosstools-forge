package org.jboss.tools.aesh.ui.internal.document;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.junit.Assert;
import org.junit.Test;

public class DocumentImplTest {
	
	private DocumentImpl documentImpl = new DocumentImpl();
	
	private Document testDocument = new Document() {
		@Override 
		public int getLineOfOffset(int offset) throws BadLocationException { 
			return offset < 10 ? offset : super.getLineOfOffset(offset);
		}
		@Override
		public int getLineOffset(int line) throws BadLocationException {
			return line < 5 ? line * 10 : super.getLineOffset(line);
		}
	};
	
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
	
	@Test
	public void testGetLineOfOffset() {
		Assert.assertEquals(-1, documentImpl.getLineOfOffset(5));
		documentImpl.delegateDocument = testDocument;
		Assert.assertEquals(5, documentImpl.getLineOfOffset(5));
		Assert.assertEquals(-1, documentImpl.getLineOfOffset(100));
	}
	
	@Test
	public void testGetLineOffset() {
		Assert.assertEquals(-1, documentImpl.getLineOffset(2));
		documentImpl.delegateDocument = testDocument;
		Assert.assertEquals(20, documentImpl.getLineOffset(2));
		Assert.assertEquals(-1, documentImpl.getLineOffset(10));
	}
	
}
