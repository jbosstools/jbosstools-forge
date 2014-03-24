package org.jboss.tools.aesh.ui.internal.viewer;

import org.jboss.tools.aesh.core.document.Document;
import org.jboss.tools.aesh.ui.internal.document.CursorListener;

public class CursorListenerImpl implements CursorListener {
	
	private TextWidget textWidget;
	private Document document;
	
	public CursorListenerImpl(TextWidget textWidget, Document document) {
		this.textWidget = textWidget;
		this.document = document;
	}

	@Override
	public void cursorMoved() {
		if (textWidget != null && !textWidget.isDisposed()) {
			textWidget.setCaretOffset(document.getCursorOffset());
		}
	}

}
