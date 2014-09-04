/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.tools.forge.ui.internal.cli;

import java.io.IOException;

import org.eclipse.jface.text.ITextViewer;
import org.jboss.forge.addon.shell.spi.Terminal;

/**
 * A {@link Terminal} based on an {@link ITextViewer}
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class TextViewerTerminal implements Terminal {

	private final ITextViewer textViewer;

	public TextViewerTerminal(ITextViewer textViewer) {
		this.textViewer = textViewer;
	}

	@Override
	public void initialize() {
	}

	@Override
	public void close() throws IOException {
	}

	@Override
	public int getHeight() {
		// TODO: Get REAL height info
		return 24;
	}

	@Override
	public int getWidth() {
		// TODO: Get REAL width info
		return 80;
	}

}
