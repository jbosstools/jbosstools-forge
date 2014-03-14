package org.jboss.tools.aesh.ui.internal;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class AeshUIPlugin extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "org.jboss.tools.forge.aesh.ui"; 

	private static AeshUIPlugin plugin;
	
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public static AeshUIPlugin getDefault() {
		return plugin;
	}

	public static void log(Throwable t) {
		getDefault().getLog().log(newErrorStatus("Error logged from Aesh UI Plugin: ", t)); 
	}
	
	private static IStatus newErrorStatus(String message, Throwable exception) {
		return new Status(IStatus.ERROR, PLUGIN_ID, IStatus.INFO, message, exception);
	}
	
}