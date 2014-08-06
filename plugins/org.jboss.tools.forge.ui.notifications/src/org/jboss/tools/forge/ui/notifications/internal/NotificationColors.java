/**
 * Copyright (c) Red Hat, Inc., contributors and others 2013 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.forge.ui.notifications.internal;

import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

public class NotificationColors {
	
	private static ColorRegistry REGISTRY = null;
	
	private static ColorRegistry getRegistry() {
		if (REGISTRY == null) {
			initializeRegistry();
		}
		return REGISTRY;
	}
	
	private static void initializeRegistry() {
		REGISTRY = new ColorRegistry(Display.getDefault());
		REGISTRY.put(NotificationConstants.FOREGROUND_COLOR_NAME, NotificationConstants.FOREGROUND_RGB);
		REGISTRY.put(NotificationConstants.TITLE_FOREGROUND_COLOR_NAME, NotificationConstants.TITLE_FOREGROUND_RGB);
		REGISTRY.put(NotificationConstants.GRADIENT_FOREGROUND_COLOR_NAME, NotificationConstants.GRADIENT_FOREGROUND_RGB);
		REGISTRY.put(NotificationConstants.GRADIENT_BACKGROUND_COLOR_NAME, NotificationConstants.GRADIENT_BACKGROUND_RGB);
		REGISTRY.put(NotificationConstants.BORDER_COLOR_NAME, NotificationConstants.BORDER_RGB);
	}
	
	public static Color getColor(String colorName) {
		return getRegistry().get(colorName);
	}
	
}
