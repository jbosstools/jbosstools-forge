package org.jboss.tools.forge.ui.notifications;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.jboss.tools.forge.ui.notifications.internal.NotificationColors;
import org.jboss.tools.forge.ui.notifications.internal.NotificationConstants;
import org.jboss.tools.forge.ui.notifications.internal.NotificationFonts;
import org.jboss.tools.forge.ui.notifications.internal.NotificationHelper;

public class NotificationDialog {

    public static void notify(String title, String message, NotificationType type) {
    	new NotificationDialog(type, title, message).show();
    }

	private static ArrayList<Shell> ACTIVE_DIALOGS = new ArrayList<Shell>();

	private Shell shell;
	private Composite clientComposite;
	private NotificationType type;
	private String title, message;

	private DisposeListener disposeListener = new DisposeListener() {
		@Override
		public void widgetDisposed(DisposeEvent event) {
			ACTIVE_DIALOGS.remove(shell);
		}
	};

	public NotificationDialog(NotificationType type, String title,
			String message) {
		this.type = type;
		this.title = title;
		this.message = message;
		createShell();
		createClientComposite();
	}

	private void createShell() {
		shell = new Shell(Display.getDefault().getActiveShell(), SWT.NO_FOCUS
				| SWT.NO_TRIM);
		shell.addDisposeListener(disposeListener);
		shell.setLayout(new FillLayout());
		shell.setForeground(NotificationColors
				.getColor(NotificationConstants.FOREGROUND_COLOR_NAME));
		shell.setBackgroundMode(SWT.INHERIT_DEFAULT);
		shell.setSize(calculateInitialSize());
		shell.setLocation(calculateInitialLocation());
		;
		shell.setAlpha(0);
	}

	private Point calculateInitialSize() {
		return new Point(NotificationConstants.DEFAULT_WIDTH,
				NotificationConstants.DEFAULT_HEIGHT);
	}

	private Point calculateInitialLocation() {
		Rectangle clientArea = Display.getDefault().getActiveShell()
				.getMonitor().getClientArea();
		int x = clientArea.x + clientArea.width
				- NotificationConstants.DEFAULT_WIDTH - 2;
		int y = clientArea.y + clientArea.height
				- NotificationConstants.DEFAULT_HEIGHT - 2;
		return new Point(x, y);
	}

	private void createClientComposite() {
		clientComposite = new Composite(shell, SWT.NONE);
		clientComposite.setLayout(createClientCompositeLayout());
		createCanvas();
		createIcon();
		createTitle();
		createMessage();
	}

	private void createIcon() {
		CLabel icon = new CLabel(clientComposite, SWT.NONE);
		icon.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING
				| GridData.HORIZONTAL_ALIGN_BEGINNING));
		icon.setImage(type.getImage());
	}

	private void createTitle() {
		CLabel titleLabel = new CLabel(clientComposite, SWT.NONE);
		titleLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_CENTER));
		titleLabel.setText(title);
		titleLabel.setForeground(NotificationColors
				.getColor(NotificationConstants.TITLE_FOREGROUND_COLOR_NAME));
		titleLabel.setFont(NotificationFonts.getFont(NotificationConstants.TITLE_FONT_NAME));
	}

	private void createMessage() {
		Label label = new Label(clientComposite, SWT.WRAP);
		label.setText(message);
		label.setFont(NotificationFonts.getFont(NotificationConstants.MESSAGE_FONT_NAME));
		label.setForeground(NotificationColors
				.getColor(NotificationConstants.FOREGROUND_COLOR_NAME));
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		label.setLayoutData(gd);
	}

	private GridLayout createClientCompositeLayout() {
		GridLayout result = new GridLayout(2, false);
		result.marginLeft = 5;
		result.marginTop = 0;
		result.marginRight = 5;
		result.marginBottom = 5;
		return result;
	}

	private void createCanvas() {
		Rectangle rect = shell.getClientArea();
		Image image = new Image(Display.getDefault(), Math.max(1, rect.width),
				rect.height);
		GC gc = new GC(image);
		fillBackground(rect, gc);
		drawBorder(rect, gc);
		gc.dispose();
		setBackground(image);
	}

	private void fillBackground(Rectangle rect, GC gc) {
		gc.setForeground(NotificationColors
				.getColor(NotificationConstants.GRADIENT_FOREGROUND_COLOR_NAME));
		gc.setBackground(NotificationColors
				.getColor(NotificationConstants.GRADIENT_BACKGROUND_COLOR_NAME));
		gc.fillGradientRectangle(rect.x, rect.y, rect.width, rect.height, true);
	}

	private void drawBorder(Rectangle rect, GC gc) {
		gc.setLineWidth(2);
		gc.setForeground(NotificationColors
				.getColor(NotificationConstants.BORDER_COLOR_NAME));
		gc.drawRectangle(rect.x + 1, rect.y + 1, rect.width - 2,
				rect.height - 2);
	}

	private void setBackground(Image image) {
		Image background = shell.getBackgroundImage();
		shell.setBackgroundImage(image);
		if (background != null) {
			background.dispose();
		}
	}

	private void moveActiveDialogs() {
		for (int i = ACTIVE_DIALOGS.size(); i > 0; i--) {
			Shell shell = ACTIVE_DIALOGS.get(i - 1);
			Point currentLocation = shell.getLocation();
			int newY = currentLocation.y - NotificationConstants.DEFAULT_HEIGHT;
			shell.setLocation(currentLocation.x, newY);
			if (newY < 0) {
				ACTIVE_DIALOGS.remove(shell);
				shell.dispose();
			}
		}
	}

	public void show() {
		moveActiveDialogs();
		shell.setVisible(true);
		ACTIVE_DIALOGS.add(shell);
		NotificationHelper.fadeIn(shell);
	}

}
