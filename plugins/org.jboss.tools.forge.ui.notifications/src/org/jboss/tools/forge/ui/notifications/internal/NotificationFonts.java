/**
 * Copyright (c) Red Hat, Inc., contributors and others 2004 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.forge.ui.notifications.internal;

import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;

public class NotificationFonts {
	
	private static FontRegistry REGISTRY = null;
	
	private static FontRegistry getRegistry() {
		if (REGISTRY == null) {
			initializeRegistry();
		}
		return REGISTRY;
	}
	
	private static FontData[] getTitleFontData() {
		Font defaultFont = REGISTRY.get(NotificationConstants.TITLE_FONT_NAME);
		FontData[] data = defaultFont.getFontData();
        data[0].setStyle(SWT.BOLD);
        data[0].height = 13;
        return data;
	}
	
	private static FontData[] getMessageFontData() {
		Font defaultFont = REGISTRY.get(NotificationConstants.MESSAGE_FONT_NAME);
		FontData[] data = defaultFont.getFontData();
        data[0].height = 12;
        return data;
	}
	
	private static void initializeRegistry() {
		REGISTRY = new FontRegistry();
		REGISTRY.put(NotificationConstants.TITLE_FONT_NAME, getTitleFontData());
		REGISTRY.put(NotificationConstants.MESSAGE_FONT_NAME, getMessageFontData());
	}
	
	public static Font getFont(String fontName) {
		return getRegistry().get(fontName);
	}

}
