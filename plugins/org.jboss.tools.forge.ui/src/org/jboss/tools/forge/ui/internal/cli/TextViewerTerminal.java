/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.tools.forge.ui.internal.cli;

import java.io.IOException;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.jboss.forge.addon.shell.spi.Terminal;

/**
 * A {@link Terminal} based on an {@link ITextViewer}
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class TextViewerTerminal implements Terminal {

	private final UpdateWidgetSizeListener controlListener = new UpdateWidgetSizeListener();
	private final ITextViewer textViewer;

	private int height;
	private int width;

	public TextViewerTerminal(final ITextViewer textViewer) {
		this.textViewer = textViewer;
	}

	@Override
	public void initialize() {
		updateSize();
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				StyledText textWidget = textViewer.getTextWidget();
				textWidget.addControlListener(controlListener);
			}
		});
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public void close() throws IOException {
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				StyledText textWidget = textViewer.getTextWidget();
				textWidget.removeControlListener(controlListener);
			}
		});
	}

	private void updateSize() {
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				StyledText textWidget = textViewer.getTextWidget();
				Point size = textWidget.getSize();
				height = size.y;
				// XXX: misaligned list of commands output
				// width = size.x
				width = 80;
			}
		});
	}

	/**
	 * Updates the size info for this widget
	 */
	private class UpdateWidgetSizeListener implements ControlListener {
		@Override
		public void controlMoved(ControlEvent e) {
			updateSize();
		}

		@Override
		public void controlResized(ControlEvent e) {
			updateSize();
		}
	}
}
