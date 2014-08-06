/**
 * Copyright (c) Red Hat, Inc., contributors and others 2013 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.aesh.ui.internal.viewer;

import org.jboss.tools.aesh.core.document.Document;
import org.jboss.tools.aesh.ui.internal.document.CursorListener;

public class CursorListenerImpl implements CursorListener {
	
	TextWidget textWidget;
	Document document;
	
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
