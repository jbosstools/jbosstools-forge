/**
 * Copyright (c) Red Hat, Inc., contributors and others 2004 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.forge.ui.notifications.internal;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class NotificationsPlugin extends AbstractUIPlugin {
	
	public static final String PLUGIN_ID = "org.jboss.tools.forge.ui.notifications"; 

	private static NotificationsPlugin plugin;
	
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public static NotificationsPlugin getDefault() {
		return plugin;
	}
	
	public static Image getImage(String fileName) {
		Image result = plugin.getImageRegistry().get(fileName);
		try {
			if (result == null) {
				URL url = plugin.getBundle().getEntry("images/" + fileName);
				InputStream inputStream = url.openConnection().getInputStream();
				result = new Image(Display.getDefault(), inputStream);
				plugin.getImageRegistry().put(fileName, result);
			}
		} catch (IOException e) {
			log(e);
		}
		return result;
	}

	public static void log(Throwable t) {
		getDefault().getLog().log(newErrorStatus("Error logged from Forge notifications plugin: ", t)); 
	}
	
	private static IStatus newErrorStatus(String message, Throwable exception) {
		return new Status(IStatus.ERROR, PLUGIN_ID, IStatus.INFO, message, exception);
	}
	

}
