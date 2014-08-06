/**
 * Copyright (c) Red Hat, Inc., contributors and others 2013 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.forge.ui.internal;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.jboss.tools.forge.ui.notifications.NotificationDialog;
import org.jboss.tools.forge.ui.notifications.NotificationType;
import org.osgi.framework.BundleContext;

public class ForgeUIPlugin extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "org.jboss.tools.forge.ui";

	private static ForgeUIPlugin plugin;

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

	public static ForgeUIPlugin getDefault() {
		return plugin;
	}

	public static void log(Throwable t) {
		getDefault().getLog().log(
				newErrorStatus("Error logged from Forge Plugin: ", t));
	}

	private static IStatus newErrorStatus(String message, Throwable exception) {
		return new Status(IStatus.ERROR, PLUGIN_ID, IStatus.INFO, message,
				exception);
	}

	public static ImageDescriptor getForgeLogo() {
		return imageDescriptorFromPlugin(ForgeUIPlugin.PLUGIN_ID,
				"icons/page.gif");
	}

	public static ImageDescriptor getForgeIcon() {
		return imageDescriptorFromPlugin(ForgeUIPlugin.PLUGIN_ID,
				"icons/icon.png");
	}

	public static void displayMessage(final String title, final String message,
			final NotificationType type) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				NotificationDialog.notify(title, message, type);
			}
		});
	}

}
