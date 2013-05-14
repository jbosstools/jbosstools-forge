package org.jboss.tools.forge.ui.notifications;

import org.eclipse.swt.graphics.Image;
import org.jboss.tools.forge.ui.notifications.internal.NotificationsPlugin;

public enum NotificationType {
	
    ERROR(NotificationsPlugin.getImage("error.png")),
    WARN(NotificationsPlugin.getImage("warn.png")),
    INFO(NotificationsPlugin.getImage("info.png"));
    
    private Image image;

    private NotificationType(Image img) {
        image = img;
    }

    public Image getImage() {
        return image;
    }
}
