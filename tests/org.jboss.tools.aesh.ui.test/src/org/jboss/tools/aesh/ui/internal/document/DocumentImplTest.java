package org.jboss.tools.aesh.ui.internal.document;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.junit.Assert;
import org.junit.Test;

public class DocumentImplTest {
	
	private boolean cursorMoved = false;
	
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
		@Override
		public int getLineLength(int line) throws BadLocationException {
			return line < 5 ? 80 : super.getLineLength(line);
		}
	};
	
	private CursorListener testListener = new CursorListener() {
		@Override
		public void cursorMoved() {
			cursorMoved = true;
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
	
	@Test
	public void testGetLineLength() {
		Assert.assertEquals(-1, documentImpl.getLineLength(2));
		documentImpl.delegateDocument = testDocument;
		Assert.assertEquals(80, documentImpl.getLineLength(2));
		Assert.assertEquals(-1, documentImpl.getLineLength(10));
	}
	
	@Test
	public void testMoveCursorTo() {
		documentImpl.cursorListener = testListener;
		Assert.assertFalse(cursorMoved);
		Assert.assertEquals(0, documentImpl.cursorOffset);
		documentImpl.moveCursorTo(6);
		Assert.assertTrue(cursorMoved);
		Assert.assertEquals(6, documentImpl.cursorOffset);
	}
	
	@Test
	public void testGetLenght() {
		documentImpl.delegateDocument = testDocument;
		Assert.assertEquals(0, documentImpl.getLength());
		testDocument.set("blah");
		Assert.assertEquals(4, documentImpl.getLength());
	}
	
}
