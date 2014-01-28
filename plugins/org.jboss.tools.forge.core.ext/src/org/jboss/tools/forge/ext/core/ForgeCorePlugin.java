package org.jboss.tools.forge.ext.core;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.Callable;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.furnace.proxy.ClassLoaderAdapterCallback;
import org.jboss.forge.furnace.repositories.AddonRepository;
import org.jboss.forge.furnace.repositories.AddonRepositoryMode;
import org.jboss.forge.furnace.util.ClassLoaders;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.wiring.BundleWiring;

import bootpath.BootpathMarker;

public class ForgeCorePlugin extends Plugin {

	private static final String RUNTIME_PLUGIN_ID = "org.jboss.tools.forge2.runtime";

	public static final String PLUGIN_ID = "org.jboss.tools.forge.core.ext";

	private static ForgeCorePlugin plugin;

	private URLClassLoader loader;
	private Furnace furnace;

	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		furnace = getFurnace(context);
		FurnaceService.INSTANCE.setFurnace(furnace);
		plugin = this;
	}
	
	public void startFurnace() {
		if (!isFurnaceStarted()) {
			FurnaceService.INSTANCE.start(loader);
		}
	}
	
	public boolean isFurnaceStarted() {
		return FurnaceService.INSTANCE.getContainerStatus().isStarted();
	}
	
	public void stopFurnace() {
		if (isFurnaceStarted()) {
			FurnaceService.INSTANCE.stop();
		}
	}

	private Furnace getFurnace(final BundleContext context) throws Exception {
		Furnace forge = ClassLoaders.executeIn(loader, new Callable<Furnace>() {
			@Override
			public Furnace call() throws Exception {
				BundleWiring wiring = context.getBundle().adapt(
						BundleWiring.class);
				Collection<String> entries = wiring.listResources("bootpath",
						"*.jar", BundleWiring.LISTRESOURCES_RECURSE);
				Collection<URL> resources = new HashSet<URL>();
				File jarDir = File.createTempFile("forge", "jars");
				if (entries != null)
					for (String resource : entries) {
						URL jar = BootpathMarker.class.getResource("/"
								+ resource);
						if (jar != null) {
							resources.add(copy(jarDir, resource,
									jar.openStream()));
						}
					}

				loader = new URLClassLoader(resources.toArray(new URL[resources
						.size()]), null);

				Class<?> bootstrapType = loader
						.loadClass("org.jboss.forge.furnace.impl.FurnaceImpl");

				Object nativeForge = bootstrapType.newInstance();
				Furnace furnace = (Furnace) ClassLoaderAdapterCallback.enhance(
						Furnace.class.getClassLoader(), loader, nativeForge,
						Furnace.class);
				setupRepositories(furnace);
				return furnace;
			}
		});
		return forge;
	}

	/**
	 * Adds the addon-repository folder inside the runtime plugin as an
	 * {@link AddonRepository}
	 */
	private void setupRepositories(final Furnace furnace) throws IOException {
		Bundle runtimeBundle = Platform.getBundle(RUNTIME_PLUGIN_ID);
		File bundleFile = FileLocator.getBundleFile(runtimeBundle);
		furnace.addRepository(AddonRepositoryMode.IMMUTABLE, new File(
				bundleFile, "addon-repository"));
		furnace.addRepository(AddonRepositoryMode.MUTABLE, new File(
				ForgeExtPreferences.INSTANCE.getAddonDir()));
	}

	private URL copy(File directory, String name, InputStream input)
			throws IOException {
		File outputFile = new File(directory, name);

		FileOutputStream output = null;
		try {
			directory.delete();
			outputFile.getParentFile().mkdirs();
			outputFile.createNewFile();

			output = new FileOutputStream(outputFile);
			final byte[] buffer = new byte[4096];
			int read = 0;
			while ((read = input.read(buffer)) != -1) {
				output.write(buffer, 0, read);
			}
			output.flush();
		} catch (Exception e) {
			throw new RuntimeException("Could not write out jar file " + name,
					e);
		} finally {
			close(input);
			close(output);
		}
		return outputFile.toURI().toURL();
	}

	private void close(Closeable closeable) {
		try {
			if (closeable != null) {
				closeable.close();
			}
		} catch (Exception e) {
			throw new RuntimeException("Could not close stream", e);
		}
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
		stopFurnace();
	}

	public static ForgeCorePlugin getDefault() {
		return plugin;
	}

	public static void log(Throwable t) {
		getDefault().getLog().log(
				newErrorStatus("Error logged from Forge Ext Core Plugin: ", t));
	}

	private static IStatus newErrorStatus(String message, Throwable exception) {
		return new Status(IStatus.ERROR, PLUGIN_ID, IStatus.INFO, message,
				exception);
	}

}
