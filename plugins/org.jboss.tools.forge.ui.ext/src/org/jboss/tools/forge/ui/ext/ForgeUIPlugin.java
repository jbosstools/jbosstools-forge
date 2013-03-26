package org.jboss.tools.forge.ui.ext;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.jboss.forge.container.spi.ListenerRegistration;
import org.jboss.forge.projects.ProjectFactory;
import org.jboss.forge.projects.ProjectListener;
import org.jboss.tools.forge.ext.core.ForgeService;
import org.jboss.tools.forge.ui.ext.importer.ImportEclipseProjectListener;
import org.osgi.framework.BundleContext;

public class ForgeUIPlugin extends AbstractUIPlugin {

    public static final String PLUGIN_ID = "org.jboss.tools.forge.ui.ext";

    private static ForgeUIPlugin plugin;

    private ListenerRegistration<ProjectListener> projectListenerRegistration;

    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        // Register the project listener
        new Thread() {
            @Override
            public void run() {
                ForgeService forgeService = ForgeService.INSTANCE;
                while (!forgeService.getContainerStatus().isStarted()) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
                ProjectFactory projectFactory = forgeService.lookup(ProjectFactory.class);
                if (projectFactory != null) {
                    projectListenerRegistration = projectFactory.addProjectListener(new ImportEclipseProjectListener());
                }
            }
        }.start();
        plugin = this;
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        if (projectListenerRegistration != null) {
            projectListenerRegistration.removeListener();
        }
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
