package org.jboss.tools.forge.core.furnace;

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
import org.eclipse.core.runtime.Platform;
import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.furnace.repositories.AddonRepositoryMode;
import org.jboss.forge.furnace.se.FurnaceFactory;
import org.jboss.forge.furnace.util.ClassLoaders;
import org.jboss.tools.forge.core.ForgeCorePlugin;
import org.jboss.tools.forge.core.ForgeExtPreferences;
import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.BundleWiring;

import bootpath.BootpathMarker;

public class FurnaceProvider {

	public static final FurnaceProvider INSTANCE = new FurnaceProvider();

	private static final String RUNTIME_PLUGIN_ID = "org.jboss.tools.forge2.runtime";

	private URLClassLoader loader;

	private FurnaceProvider() {
	}

	public Furnace createFurnace() throws Exception {

		Furnace forge = ClassLoaders.executeIn(loader, new Callable<Furnace>() {
			@Override
			public Furnace call() throws Exception {
				BundleWiring wiring = ForgeCorePlugin.getDefault().getBundle()
						.adapt(BundleWiring.class);
				Collection<String> entries = wiring.listResources("bootpath",
						"*.jar", BundleWiring.LISTRESOURCES_RECURSE);
				Collection<URL> resources = new HashSet<>();
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

				Furnace furnace = FurnaceFactory.getInstance(loader);
				
				setupRepositories(furnace);
				return furnace;
			}
		});
		return forge;
	}

	private void setupRepositories(final Furnace furnace) throws IOException {
		Bundle runtimeBundle = Platform.getBundle(RUNTIME_PLUGIN_ID);
		File bundleFile = FileLocator.getBundleFile(runtimeBundle);
		furnace.addRepository(AddonRepositoryMode.IMMUTABLE, new File(
				bundleFile, "addon-repository"));
		furnace.addRepository(AddonRepositoryMode.MUTABLE, new File(
				ForgeExtPreferences.INSTANCE.getAddonDir()));
	}

	public void startFurnace() {
		try {
			FurnaceService.INSTANCE.setFurnace(createFurnace());
			FurnaceService.INSTANCE.start(loader);
		} catch (Exception e) {
			ForgeCorePlugin.log(e);
		}
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

}
