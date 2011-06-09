package org.jboss.tools.forge.core;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

public class ForgeCorePlugin extends Plugin {

	public static final String PLUGIN_ID = "org.jboss.tools.forge.core"; 

	private static ForgeCorePlugin plugin;
	
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

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
	
}
