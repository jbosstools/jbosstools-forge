package org.jboss.tools.forge.ui.wizard;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class WizardsPlugin extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "org.jboss.tools.forge.ui.scaffold"; 

	private static WizardsPlugin plugin;
	
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public static WizardsPlugin getDefault() {
		return plugin;
	}

	public static void log(Throwable t) {
		getDefault().getLog().log(newErrorStatus("Error logged from Forge scaffold plugin: ", t)); 
	}
	
	private static IStatus newErrorStatus(String message, Throwable exception) {
		return new Status(IStatus.ERROR, PLUGIN_ID, IStatus.INFO, message, exception);
	}
	
}
