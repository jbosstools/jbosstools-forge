package org.jboss.tools.forge.ui.ext;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.ProjectListener;
import org.jboss.forge.furnace.spi.ListenerRegistration;
import org.jboss.tools.forge.ext.core.FurnaceService;
import org.jboss.tools.forge.ui.ext.importer.ImportEclipseProjectListener;
import org.jboss.tools.forge.ui.ext.listeners.EventBus;
import org.jboss.tools.forge.ui.ext.listeners.PickUpListener;
import org.jboss.tools.forge.ui.ext.listeners.RefreshListener;
import org.osgi.framework.BundleContext;

public class ForgeUIPlugin extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "org.jboss.tools.forge.ui.ext";

	private static ForgeUIPlugin plugin;

	private ListenerRegistration<ProjectListener> projectListenerRegistration;

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		ExecutorService executor = Executors.newSingleThreadExecutor();
		// Register the project listener
		executor.submit(new Runnable() {
			@Override
			public void run() {
				FurnaceService forgeService = FurnaceService.INSTANCE;
				while (!forgeService.getContainerStatus().isStarted()) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						break;
					}
				}
				ProjectFactory projectFactory;
				while ((projectFactory = forgeService
						.lookup(ProjectFactory.class)) == null) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						break;
					}
				}
				if (projectFactory != null) {
					projectListenerRegistration = projectFactory
							.addProjectListener(ImportEclipseProjectListener.INSTANCE);
					EventBus.INSTANCE
							.register(ImportEclipseProjectListener.INSTANCE);
				}
				EventBus.INSTANCE.register(RefreshListener.INSTANCE);
				EventBus.INSTANCE.register(PickUpListener.INSTANCE);
			}
		});
		executor.shutdown();
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
}
