/**
 * Copyright (c) Red Hat, Inc., contributors and others 2004 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.aesh.ui.internal.viewer;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.swt.custom.StyleRange;
import org.jboss.tools.aesh.core.document.Document;
import org.jboss.tools.aesh.ui.internal.document.StyleImpl;

public class DocumentListenerImpl implements IDocumentListener {

	TextWidget textWidget;
	Document document;

	public DocumentListenerImpl(TextWidget textWidget, Document document) {
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
