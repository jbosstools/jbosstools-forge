/**
 * Copyright (c) Red Hat, Inc., contributors and others 2013 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.forge.ui.notifications.internal;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class NotificationHelper {
	
	private static class FadeInRunnable implements Runnable {
		private Shell shell;
		private int currentAlpha = 0;
		public FadeInRunnable(Shell shell) {
			this.shell = shell;
		}
		@Override
		public void run() {
			if (shell == null || shell.isDisposed()) return;
			currentAlpha += NotificationConstants.FADE_IN_STEP;
			if (currentAlpha > NotificationConstants.FINAL_ALPHA) {
				shell.setAlpha(NotificationConstants.FINAL_ALPHA);
				startTimer(shell);
				return;
			}
			shell.setAlpha(currentAlpha);
			Display.getDefault().timerExec(NotificationConstants.FADE_TIMER, this);
		}
	}
	
	private static class FadeOutRunnable implements Runnable {
		private Shell shell;
		private int currentAlpha= NotificationConstants.FINAL_ALPHA;
		public FadeOutRunnable(Shell shell) {
			this.shell = shell;
		}
		@Override
		public void run() {
			if (shell == null || shell.isDisposed()) return;
			currentAlpha -= NotificationConstants.FADE_OUT_STEP;
			if (currentAlpha <= 0) {
				shell.setAlpha(0);
				shell.dispose();
				return;
			}
			shell.setAlpha(currentAlpha);
			Display.getDefault().timerExec(NotificationConstants.FADE_TIMER, this);
		}
	}

	public static void fadeIn(final Shell shell) {
		Display.getDefault().timerExec(
				NotificationConstants.FADE_TIMER, 
				new FadeInRunnable(shell));
	}

	private static void startTimer(final Shell shell) {
		Runnable run = new Runnable() {
			@Override
			public void run() {
				if (shell == null || shell.isDisposed()) return;
				fadeOut(shell);
			}
		};
		Display.getDefault().timerExec(NotificationConstants.DISPLAY_TIME, run);
	}

	private static void fadeOut(final Shell shell) {
		Display.getDefault().timerExec(
				NotificationConstants.FADE_TIMER, 
				new FadeOutRunnable(shell));
	}

}
