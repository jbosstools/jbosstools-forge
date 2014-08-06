/**
 * Copyright (c) Red Hat, Inc., contributors and others 2013 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
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
