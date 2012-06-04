package org.jboss.tools.forge.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IProcess;
import org.osgi.framework.BundleContext;

public class ForgeCorePlugin extends Plugin {

	public static final String PLUGIN_ID = "org.jboss.tools.forge.core";

	private static ForgeCorePlugin plugin;

	private static Thread shutdownHook;
	private static List<IProcess> processes = new ArrayList<IProcess>();

	public void start(BundleContext context) throws Exception {
		super.start(context);
		initializeShutdownHook();
		plugin = this;
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

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public static ForgeCorePlugin getDefault() {
		return plugin;
	}

	public static void log(Throwable t) {
		getDefault().getLog().log(
				newErrorStatus("Error logged from Forge Core Plugin: ", t));
	}

	private static IStatus newErrorStatus(String message, Throwable exception) {
		return new Status(IStatus.ERROR, PLUGIN_ID, IStatus.INFO, message,
				exception);
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
