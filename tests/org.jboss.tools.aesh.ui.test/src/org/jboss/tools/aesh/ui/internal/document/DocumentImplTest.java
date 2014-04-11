package org.jboss.tools.aesh.ui.internal.document;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.swt.custom.StyleRange;
import org.jboss.tools.aesh.core.document.Style;
import org.jboss.tools.aesh.ui.internal.util.ColorConstants;
import org.jboss.tools.aesh.ui.internal.util.FontManager;
import org.junit.Assert;
import org.junit.Test;

public class DocumentImplTest {
	
	private boolean cursorMoved = false;
	private int replacedPos = -1, replacedLength = -1;
	private String replacedText = null;
	
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
		@Override
		public void replace(int pos, int length, String text) {
			replacedPos = pos;
			replacedLength = length;
			replacedText = text;
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
	public void testReset() {
		documentImpl.delegateDocument = testDocument;
		documentImpl.cursorListener = testListener;
		testDocument.set("i blah");
		documentImpl.cursorOffset = 6;
		StyleRange oldRange = new StyleRange(2, 4, ColorConstants.BLUE, ColorConstants.CYAN);
		oldRange.font = FontManager.INSTANCE.getItalicBold();
		StyleImpl oldStyle = new StyleImpl(oldRange);
		documentImpl.currentStyle = oldStyle;
		Assert.assertEquals("1", 4, documentImpl.currentStyle.getLength());
		Assert.assertEquals("2", 2, documentImpl.currentStyle.getStart());
		Assert.assertEquals("3", ColorConstants.BLUE, documentImpl.currentStyle.styleRange.foreground);
		Assert.assertEquals("4", ColorConstants.CYAN, documentImpl.currentStyle.styleRange.background);
		Assert.assertEquals("5", "i blah", documentImpl.delegateDocument.get());
		Assert.assertEquals("6", 6,  documentImpl.cursorOffset);
		Assert.assertFalse(cursorMoved);
		documentImpl.reset();
		Assert.assertEquals(0, documentImpl.currentStyle.getLength());
		Assert.assertEquals(0, documentImpl.currentStyle.getStart());
		Assert.assertEquals(ColorConstants.BLACK, documentImpl.currentStyle.styleRange.foreground);
		Assert.assertEquals(ColorConstants.WHITE, documentImpl.currentStyle.styleRange.background);
		Assert.assertEquals("", documentImpl.delegateDocument.get());
		Assert.assertEquals(0, documentImpl.cursorOffset);
		Assert.assertTrue(cursorMoved);
	}

	@Test
	public void testGetLenght() {
		documentImpl.delegateDocument = testDocument;
		Assert.assertEquals(0, documentImpl.getLength());
		testDocument.set("blah");
		Assert.assertEquals(4, documentImpl.getLength());
	}
	
	@Test
	public void testReplace() {
		documentImpl.delegateDocument = testDocument;
		Assert.assertEquals(-1, replacedPos);
		Assert.assertEquals(-1, replacedLength);
		Assert.assertNull(replacedText);
		documentImpl.replace(5,  9, "blahblah");
		Assert.assertEquals(5, replacedPos);
		Assert.assertEquals(9,  replacedLength);
		Assert.assertEquals("blahblah", replacedText);
	}
	
	@Test
	public void testRestoreCursor() {
		documentImpl.cursorListener = testListener;
		documentImpl.cursorOffset = 45;
		documentImpl.savedCursor = 23;
		documentImpl.restoreCursor();
		Assert.assertEquals(23, documentImpl.cursorOffset);
		Assert.assertTrue(cursorMoved);
	}
	
	@Test
	public void testSaveCursor() {
		documentImpl.savedCursor = -1;
		documentImpl.cursorOffset = 34;
		documentImpl.saveCursor();
		Assert.assertEquals(34, documentImpl.savedCursor);
	}
	
	@Test
	public void testNewStyleFromCurrent() {
		documentImpl.delegateDocument = testDocument;
		StyleRange oldRange = new StyleRange(5, 10, ColorConstants.BLUE, ColorConstants.CYAN);
		oldRange.font = FontManager.INSTANCE.getItalicBold();
		StyleImpl oldStyle = new StyleImpl(oldRange);
		documentImpl.currentStyle = oldStyle;
		Style style = documentImpl.newStyleFromCurrent();
		Assert.assertTrue(style instanceof StyleImpl);
		StyleImpl styleImpl = (StyleImpl)style;
		Assert.assertEquals(0, styleImpl.getLength());
		Assert.assertEquals(15, styleImpl.getStart());
		StyleRange styleRange = styleImpl.styleRange;
		Assert.assertEquals(ColorConstants.BLUE, styleRange.foreground);
		Assert.assertEquals(ColorConstants.CYAN, styleRange.background);
		Assert.assertEquals(FontManager.INSTANCE.getItalicBold(), styleRange.font);
		Assert.assertEquals(15, styleRange.start);
		Assert.assertEquals(0, styleRange.length);
	}
	
	@Test
	public void testSetCurrentStyle() {
		Style newStyle = StyleImpl.getDefault();
		Assert.assertNotNull(documentImpl.currentStyle);
		Assert.assertNotEquals(documentImpl.currentStyle, newStyle);
		documentImpl.setCurrentStyle(newStyle);
		Assert.assertEquals(documentImpl.currentStyle, newStyle);
	}
	
	@Test
	public void testSetDefaultStyle() {
		documentImpl.delegateDocument = testDocument;
		StyleRange oldRange = new StyleRange(0, 0, ColorConstants.BLUE, ColorConstants.CYAN);
		oldRange.font = FontManager.INSTANCE.getItalicBold();
		StyleImpl oldStyle = new StyleImpl(oldRange);
		documentImpl.currentStyle = oldStyle;
		Assert.assertEquals(0, documentImpl.currentStyle.getLength());
		Assert.assertEquals(0, documentImpl.currentStyle.getStart());
		Assert.assertEquals(ColorConstants.BLUE, documentImpl.currentStyle.styleRange.foreground);
		Assert.assertEquals(ColorConstants.CYAN, documentImpl.currentStyle.styleRange.background);
		testDocument.set("blah");
		documentImpl.setDefaultStyle();
		Assert.assertEquals(0, documentImpl.currentStyle.getLength());
		Assert.assertEquals(4, documentImpl.currentStyle.getStart());
		Assert.assertEquals(ColorConstants.BLACK, documentImpl.currentStyle.styleRange.foreground);
		Assert.assertEquals(ColorConstants.WHITE, documentImpl.currentStyle.styleRange.background);
		Assert.assertEquals(FontManager.INSTANCE.getDefault(), documentImpl.currentStyle.styleRange.font);
	}
	
	@Test
	public void testGetDelegate() {
		Assert.assertNotEquals(testDocument, documentImpl.getDelegate());
		documentImpl.delegateDocument = testDocument;
		Assert.assertEquals(testDocument, documentImpl.getDelegate());
	}
	
}
