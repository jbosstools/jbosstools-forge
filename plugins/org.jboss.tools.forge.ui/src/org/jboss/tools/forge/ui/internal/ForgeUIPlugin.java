package org.jboss.tools.forge.ui.internal;

import java.util.Properties;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.embedder.AbstractMavenConfigurationChangeListener;
import org.eclipse.m2e.core.embedder.IMavenConfiguration;
import org.eclipse.m2e.core.embedder.MavenConfigurationChangeEvent;
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
		setMavenSettings();
		plugin = this;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	private void setMavenSettings() {
		IMavenConfiguration mavenConfig = MavenPlugin.getMavenConfiguration();
		mavenConfig
				.addConfigurationChangeListener(new AbstractMavenConfigurationChangeListener() {
					@Override
					public void mavenConfigurationChange(
							MavenConfigurationChangeEvent event)
							throws CoreException {
						registerSystemProperties();
					}
				});
		registerSystemProperties();
	}

	private void registerSystemProperties() {
		IMavenConfiguration mavenConfig = MavenPlugin.getMavenConfiguration();
		Properties properties = System.getProperties();

		// Register user settings file
		String userSettingsFile = mavenConfig.getUserSettingsFile();
		if (userSettingsFile != null) {
			properties.setProperty("org.apache.maven.user-settings",
					userSettingsFile);
		} else {
			properties.remove("org.apache.maven.user-settings");
		}

		// Register global settings file
		String globalSettingsFile = mavenConfig.getGlobalSettingsFile();
		if (globalSettingsFile != null) {
			properties.setProperty("org.apache.maven.global-settings",
					globalSettingsFile);
		} else {
			properties.remove("org.apache.maven.global-settings");
		}
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
