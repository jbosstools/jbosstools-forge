package org.jboss.tools.aesh.ui.internal.viewer;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.swt.custom.StyleRange;
import org.jboss.tools.aesh.ui.internal.document.DocumentImpl;
import org.jboss.tools.aesh.ui.internal.document.StyleImpl;

public class DocumentListenerImpl implements IDocumentListener {

	private TextWidget textWidget;
	private DocumentImpl document;

	public DocumentListenerImpl(TextWidget textWidget, DocumentImpl document) {
		this.textWidget = textWidget;
		this.document = document;
	}

	@Override
    public void documentAboutToBeChanged(DocumentEvent event) {
    }

	@Override
    public void documentChanged(final DocumentEvent event) {
        if (textWidget != null && !textWidget.isDisposed()) {
            int lineCount = textWidget.getLineCount();
            textWidget.setTopIndex(lineCount - 1);
            StyleImpl style = (StyleImpl)document.getCurrentStyle();
			StyleRange styleRange = style.getStyleRange();
			if (styleRange != null &&
					event.getLength() == 0 &&
					styleRange.start >= 0 &&
					styleRange.start <= document.getLength() &&
					styleRange.length >= 0 &&
					styleRange.start + styleRange.length <= document.getLength()) {
 				textWidget.setStyleRange(styleRange);
			}
        }
    }

}
