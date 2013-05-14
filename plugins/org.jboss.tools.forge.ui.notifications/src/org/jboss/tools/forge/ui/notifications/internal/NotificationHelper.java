package org.jboss.tools.forge.ui.notifications.internal;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class NotificationHelper {

	public static void fadeIn(final Shell shell) {
		Runnable run = new Runnable() {
			@Override
			public void run() {
				if (shell == null || shell.isDisposed()) return;
				int currentAlpha = shell.getAlpha();
				currentAlpha += NotificationConstants.FADE_IN_STEP;
				if (currentAlpha > NotificationConstants.FINAL_ALPHA) {
					shell.setAlpha(NotificationConstants.FINAL_ALPHA);
					startTimer(shell);
					return;
				}
				shell.setAlpha(currentAlpha);
				Display.getDefault().timerExec(NotificationConstants.FADE_TIMER, this);
			}
		};
		Display.getDefault().timerExec(NotificationConstants.FADE_TIMER, run);
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
		final Runnable run = new Runnable() {
			@Override
			public void run() {
				if (shell == null || shell.isDisposed()) return;
				int currentAlpha = shell.getAlpha();
				currentAlpha -= NotificationConstants.FADE_OUT_STEP;
				if (currentAlpha <= 0) {
					shell.setAlpha(0);
					shell.dispose();
					return;
				}
				shell.setAlpha(currentAlpha);
				Display.getDefault().timerExec(NotificationConstants.FADE_TIMER, this);
			}

		};
		Display.getDefault().timerExec(NotificationConstants.FADE_TIMER, run);

	}

}
