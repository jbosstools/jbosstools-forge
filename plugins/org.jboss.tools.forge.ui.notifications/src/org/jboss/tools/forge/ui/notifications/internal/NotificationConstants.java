/**
 * Copyright (c) Red Hat, Inc., contributors and others 2004 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.forge.ui.notifications.internal;

import org.eclipse.swt.graphics.RGB;

public class NotificationConstants {
	
	public static String FOREGROUND_COLOR_NAME = "NotificationConstants.ForegroundColor";
	public static String TITLE_FOREGROUND_COLOR_NAME = "NotificationConstants.TitleForegroundColor";
    public static String GRADIENT_FOREGROUND_COLOR_NAME = "NotificationConstants.GradientForegroundColor";
    public static String GRADIENT_BACKGROUND_COLOR_NAME = "NotificationConstants.GradientBackgroundColor";
    public static String BORDER_COLOR_NAME  = "NotificationConstants.BorderColor";
    public static String TITLE_FONT_NAME = "NotificationConstants.TitleFont";
    public static String MESSAGE_FONT_NAME = "NotificationConstants.MessageFont";
	public static RGB FOREGROUND_RGB = new RGB(40, 73, 97);
	public static RGB TITLE_FOREGROUND_RGB = FOREGROUND_RGB;
    public static RGB GRADIENT_FOREGROUND_RGB = new RGB(226, 239, 249);
    public static RGB GRADIENT_BACKGROUND_RGB = new RGB(177, 211, 243);
    public static RGB BORDER_RGB  = new RGB(40, 73, 97);
    public static int DEFAULT_WIDTH = 350;
    public static int DEFAULT_HEIGHT = 100;
    public static int FINAL_ALPHA = 225;
    public static int FADE_IN_STEP = 20;
    public static int FADE_OUT_STEP = 8;
    public static int FADE_TIMER = 50;
    public static int DISPLAY_TIME  = 4500;
    
}
