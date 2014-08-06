/**
 * Copyright (c) Red Hat, Inc., contributors and others 2013 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.forge.ui.internal.ext.dialog;

import org.eclipse.jface.dialogs.ProgressIndicator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.wizard.ProgressMonitorPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

/**
 * A standard implementation of an IProgressMonitor. It consists of a label
 * displaying the task and subtask name, and a progress indicator to show
 * progress. In contrast to <code>ProgressMonitorDialog</code> this class only
 * implements <code>IProgressMonitor</code>.
 */
public class ForgeProgressMonitorPart extends ProgressMonitorPart {

	private final class CancelListener implements Listener {

		@Override
		public void handleEvent(Event event) {
			InterruptableProgressMonitor progressMonitor = dialog
					.getProgressMonitor();
			if (progressMonitor != null && dialog.isRunning()) {
				if (progressMonitor.isPreviouslyCancelled()) {
					dialog.setErrorMessage("Attempting to force stop...");
					progressMonitor.setCanceled(true);
					setCanceled(true);
					if (fStopButton != null) {
						fStopButton.setEnabled(false);
					}
				} else {
					progressMonitor.setCanceled(true);
					dialog.setErrorMessage("Cancel requested: click again to attempt force stop.");
				}
			}
		}

	}

	private ToolItem fStopButton;
	private ToolBar fToolBar;
	private ForgeCommandDialog dialog;

	public ForgeProgressMonitorPart(Composite parent, Layout layout,
			ForgeCommandDialog dialog) {
		super(parent, layout, true);
		this.dialog = dialog;
	}

	/**
	 * Creates the progress monitor's UI parts and layouts them according to the
	 * given layout. If the layout is <code>null</code> the part's default
	 * layout is used.
	 * 
	 * @param layout
	 *            The layout for the receiver.
	 * @param progressIndicatorHeight
	 *            The suggested height of the indicator
	 */
	protected void initialize(Layout layout, int progressIndicatorHeight) {
		if (layout == null) {
			GridLayout l = new GridLayout();
			l.marginWidth = 0;
			l.marginHeight = 0;
			layout = l;
		}
		int numColumns = 2;
		setLayout(layout);

		if (layout instanceof GridLayout)
			((GridLayout) layout).numColumns = numColumns;

		fLabel = new Label(this, SWT.LEFT);
		fLabel.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true,
				false, numColumns, 1));

		if (progressIndicatorHeight == SWT.DEFAULT) {
			GC gc = new GC(fLabel);
			FontMetrics fm = gc.getFontMetrics();
			gc.dispose();
			progressIndicatorHeight = fm.getHeight();
		}

		fProgressIndicator = new ProgressIndicator(this);
		GridData gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = false;
		gd.verticalAlignment = GridData.CENTER;
		gd.heightHint = progressIndicatorHeight;
		fProgressIndicator.setLayoutData(gd);

		fToolBar = new ToolBar(this, SWT.FLAT);

		gd = new GridData();
		gd.grabExcessHorizontalSpace = false;
		gd.grabExcessVerticalSpace = false;
		gd.verticalAlignment = GridData.CENTER;
		fToolBar.setLayoutData(gd);
		fStopButton = new ToolItem(fToolBar, SWT.PUSH);
		// It would have been nice to use the fCancelListener, but that
		// listener operates on the fCancelComponent which must be a control.
		fStopButton.addListener(SWT.Selection, new CancelListener());
		final Image stopImage = ImageDescriptor
				.createFromFile(ForgeProgressMonitorPart.class,
						"images/stop.gif").createImage(getDisplay()); //$NON-NLS-1$
		final Cursor arrowCursor = new Cursor(this.getDisplay(),
				SWT.CURSOR_ARROW);
		fToolBar.setCursor(arrowCursor);
		fStopButton.setImage(stopImage);
		fStopButton.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				stopImage.dispose();
				arrowCursor.dispose();
			}
		});
		fStopButton.setEnabled(false);
		fStopButton.setToolTipText(JFaceResources
				.getString("ProgressMonitorPart.cancelToolTip")); //$NON-NLS-1$
	}

	@Override
	public boolean isCanceled() {
		return fIsCanceled;
	}

	public void attachToCancelComponent(Control cancelComponent) {
		cancelComponent.addListener(SWT.Selection, new CancelListener());
		setCancelEnabled(true);
	}

	public void setCancelEnabled(boolean enabled) {
		if (fStopButton != null && !fStopButton.isDisposed()) {
			fStopButton.setEnabled(enabled);
			if (enabled)
				fToolBar.setFocus();
		}
	}
}
