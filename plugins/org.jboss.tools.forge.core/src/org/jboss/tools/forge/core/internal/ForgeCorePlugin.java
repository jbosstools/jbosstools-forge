/**
 * Copyright (c) Red Hat, Inc., contributors and others 2013 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.forge.core.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.embedder.IMavenConfigurationChangeListener;
import org.eclipse.m2e.core.embedder.IMavenConfiguration;
import org.eclipse.m2e.core.embedder.MavenConfigurationChangeEvent;
import org.jboss.tools.forge.core.runtime.ForgeRuntime;
import org.jboss.tools.usage.event.UsageEvent;
import org.jboss.tools.usage.event.UsageEventType;
import org.jboss.tools.usage.event.UsageReporter;
import org.osgi.framework.BundleContext;

public class ForgeCorePlugin extends Plugin {

	public static final String PLUGIN_ID = "org.jboss.tools.forge.core";

	private static ForgeCorePlugin plugin;

	private static Thread shutdownHook;
	private static List<IProcess> processes = new ArrayList<>();

	private UsageEventType forgeStartEventType;

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		setMavenSettings();
		initializeShutdownHook();
		plugin = this;
		initializeUsageReporting();
	}

	private void initializeUsageReporting() {
		forgeStartEventType = new UsageEventType("forge", UsageEventType.getVersion(this), null, "start",
				"Forge Runtime Version", "major.minor.micro.identifier");
		UsageReporter.getInstance().registerEvent(forgeStartEventType);
	}

	public void sendStartEvent(ForgeRuntime runtime) {
		UsageEventType startEventType = getForgeStartEventType();
		UsageEvent startEvent = startEventType.event(runtime.getVersion());
		UsageReporter.getInstance().trackEvent(startEvent);
	}

	private void setMavenSettings() {
		IMavenConfiguration mavenConfig = MavenPlugin.getMavenConfiguration();
		mavenConfig.addConfigurationChangeListener(event -> {
				registerSystemProperties();
		});
		registerSystemProperties();
	}

	private void registerSystemProperties() {
		IMavenConfiguration mavenConfig = MavenPlugin.getMavenConfiguration();
		Properties properties = System.getProperties();

		// Register user settings file
		String userSettingsFile = mavenConfig.getUserSettingsFile();
		if (userSettingsFile != null) {
			properties.setProperty("org.apache.maven.user-settings", userSettingsFile);
		} else {
			properties.remove("org.apache.maven.user-settings");
		}

		// Register global settings file
		String globalSettingsFile = mavenConfig.getGlobalSettingsFile();
		if (globalSettingsFile != null) {
			properties.setProperty("org.apache.maven.global-settings", globalSettingsFile);
		} else {
			properties.remove("org.apache.maven.global-settings");
		}
	}

	public UsageEventType getForgeStartEventType() {
		return forgeStartEventType;
	}

	private void initializeShutdownHook() {
		if (shutdownHook == null) {
			shutdownHook = new Thread(new Runnable() {
				@Override
				public void run() {
					for (IProcess process : processes) {
						if (process != null && process.canTerminate()) {
							try {
								process.terminate();
							} catch (DebugException e) {
								log(e);
							}
						}
					}
				}
			});
			Runtime.getRuntime().addShutdownHook(shutdownHook);
		}
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public static ForgeCorePlugin getDefault() {
		return plugin;
	}

	public static void log(Throwable t) {
		getDefault().getLog().log(newErrorStatus("Error logged from Forge Core Plugin: ", t));
	}

	private static IStatus newErrorStatus(String message, Throwable exception) {
		return new Status(IStatus.ERROR, PLUGIN_ID, IStatus.INFO, message, exception);
	}

	public static void addForgeProcess(IProcess process) {
		processes.add(process);
	}

	public static void removeForgeProcess(IProcess process) {
		processes.remove(process);
	}

	public static void log(IStatus status) {
		ResourcesPlugin.getPlugin().getLog().log(status);
	}

	public static void logErrorMessage(String message) {
		log(IStatus.ERROR, message);
	}

	public static void logInfoMessage(String message) {
		log(IStatus.INFO, message);
	}

	private static void log(int status, String message) {
		log(new Status(status, PLUGIN_ID, status, message, null));
	}
}
