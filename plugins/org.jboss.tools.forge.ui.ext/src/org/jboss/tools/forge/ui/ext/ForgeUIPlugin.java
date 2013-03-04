package org.jboss.tools.forge.ui.ext;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class ForgeUIPlugin extends AbstractUIPlugin {

    public static final String PLUGIN_ID = "org.jboss.tools.forge.ui.ext";

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
        getDefault().getLog().log(newErrorStatus("Error logged from Forge Plugin: ", t));
    }

    private static IStatus newErrorStatus(String message, Throwable exception) {
        return new Status(IStatus.ERROR, PLUGIN_ID, IStatus.INFO, message, exception);
    }

    public static ImageDescriptor getForgeLogo() {
        return imageDescriptorFromPlugin(ForgeUIPlugin.PLUGIN_ID, "icons/forge.png");
    }

}
