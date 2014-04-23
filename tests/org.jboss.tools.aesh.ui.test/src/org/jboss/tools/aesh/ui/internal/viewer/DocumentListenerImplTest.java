package org.jboss.tools.aesh.ui.internal.viewer;


import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.widgets.Shell;
import org.jboss.tools.aesh.core.document.Document;
import org.jboss.tools.aesh.core.document.Style;
import org.jboss.tools.aesh.ui.internal.document.StyleImpl;
import org.jboss.tools.aesh.ui.test.util.TestDocument;
import org.junit.Assert;
import org.junit.Test;


public class DocumentListenerImplTest {
	
	private StyleRange testStyleRange = null;
	private StyleImpl testStyle = null;
	private int documentLength = 0;
	
	private StyleRange currentStyleRange = null;
	
	private Document testDocument = new TestDocument() {
		@Override public Style getCurrentStyle() { return testStyle; }
		@Override public int getLength() { return documentLength; }
	};
	
	private TextWidget testTextWidget = new TextWidget(new Shell(), SWT.NONE) {
		@Override public void setStyleRange(StyleRange styleRange) { currentStyleRange = styleRange; }
	};
	
	private DocumentListenerImpl testDocumentListenerImpl = 
			new DocumentListenerImpl(testTextWidget, testDocument);
	
	@Test
	public void testConstructor() {
		Assert.assertEquals(testTextWidget, testDocumentListenerImpl.textWidget);
		Assert.assertEquals(testDocument, testDocumentListenerImpl.document);
	}
	
	@Test
	public void testDocumentAboutToBeChanged() {
		// documentAboutToBeChanged() has an empty body
		testDocumentListenerImpl.documentAboutToBeChanged(new DocumentEvent());
		Assert.assertTrue(true);
	}
	
	@Test
	public void testDocumentChanged() {
		// no strange things happen when textWidget == null
		testDocumentListenerImpl.textWidget = null;
		testDocumentListenerImpl.documentChanged(new DocumentEvent());		
		Assert.assertTrue(true);
		// if textWidget != null and !textWidget.isDisposed()
		testDocumentListenerImpl.textWidget = testTextWidget;
		testStyle = new StyleImpl(new StyleRange());
		testTextWidget.setText("blah\nblah\nblah\nblah\nblah");
		testDocumentListenerImpl.documentChanged(new DocumentEvent());
		Assert.assertEquals(4, testTextWidget.getTopIndex());
		// if styleRange == null
		currentStyleRange = null;
		testStyle = new StyleImpl(null);
		testDocumentListenerImpl.documentChanged(new DocumentEvent());
		Assert.assertNull(currentStyleRange);
		// if event.getLength() != null
		currentStyleRange = null;
		testStyleRange = new StyleRange();
		testStyle = new StyleImpl(testStyleRange);
		DocumentEvent documentEvent = new DocumentEvent();
		documentEvent.fLength = 5;
		testDocumentListenerImpl.documentChanged(documentEvent);
		Assert.assertNull(currentStyleRange);
		// if styleRange.start < 0
		currentStyleRange = null;
		testStyleRange.start = -5;
		testDocumentListenerImpl.documentChanged(new DocumentEvent());
		Assert.assertNull(currentStyleRange);
		// if styleRange.start > document.getLength()
		currentStyleRange = null;
		testStyleRange.start = 50;
		documentLength = 20;
		testDocumentListenerImpl.documentChanged(new DocumentEvent());
		Assert.assertNull(currentStyleRange);
		// if styleRange.length < 0
		currentStyleRange = null;
		testStyleRange.length = -5;
		testDocumentListenerImpl.documentChanged(new DocumentEvent());
		Assert.assertNull(currentStyleRange);
		// if styleRange.start + styleRange.length > document.getLength()
		currentStyleRange = null;
		testStyleRange.start = 10;
		testStyleRange.length = 15;
		documentLength = 20;
		testDocumentListenerImpl.documentChanged(new DocumentEvent());
		Assert.assertNull(currentStyleRange);
		// if all conditions are met
		currentStyleRange = null;
		testStyleRange.start = 5;
		testStyleRange.length = 10;
		documentLength = 20;
		testDocumentListenerImpl.documentChanged(new DocumentEvent());
		Assert.assertEquals(testStyleRange, currentStyleRange);
		// if textWidget is disposed
		currentStyleRange = null;
		testTextWidget.dispose();
		testDocumentListenerImpl.documentChanged(new DocumentEvent());
		Assert.assertNull(currentStyleRange);
	}
	
	
	
}
