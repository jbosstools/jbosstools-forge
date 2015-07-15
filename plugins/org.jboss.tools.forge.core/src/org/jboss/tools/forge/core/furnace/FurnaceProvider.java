/**
 * Copyright (c) Red Hat, Inc., contributors and others 2013 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.forge.core.furnace;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.furnace.repositories.AddonRepositoryMode;
import org.jboss.forge.furnace.se.FurnaceFactory;
import org.jboss.forge.furnace.util.AddonCompatibilityStrategies;
import org.jboss.tools.forge.core.furnace.repository.IFurnaceRepository;
import org.jboss.tools.forge.core.internal.ForgeCorePlugin;
import org.jboss.tools.forge.core.internal.furnace.CompositeFurnaceClassLoader;
import org.jboss.tools.forge.core.internal.furnace.repository.FurnaceRepositoryManager;
import org.jboss.tools.forge.core.preferences.ForgeCorePreferences;
import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.BundleWiring;

import bootpath.BootpathMarker;

public class FurnaceProvider {

	public static final FurnaceProvider INSTANCE = new FurnaceProvider();

	private static final String RUNTIME_PLUGIN_ID = "org.jboss.tools.forge.runtime";

	private FurnaceProvider() {
	}

	public Furnace createFurnace() throws Exception {

		BundleWiring wiring = ForgeCorePlugin.getDefault().getBundle()
				.adapt(BundleWiring.class);
		Collection<String> entries = wiring.listResources("bootpath", "*.jar",
				BundleWiring.LISTRESOURCES_RECURSE);
		Collection<URL> resources = new HashSet<>();
		File jarDir = File.createTempFile("forge", "jars");
		if (entries != null)
			for (String resource : entries) {
				URL jar = BootpathMarker.class.getResource("/" + resource);
				if (jar != null) {
					resources.add(copy(jarDir, resource, jar.openStream()));
				}
			}

		ClassLoader furnaceLoader = new URLClassLoader(
				resources.toArray(new URL[resources.size()]), null);

		// add repositories from the furnace repository extension point
		FurnaceRepositoryManager repositoryManager = FurnaceRepositoryManager
				.getDefault();
		List<IFurnaceRepository> repos = repositoryManager.getRepositories();

		ArrayList<ClassLoader> loaders = new ArrayList<>();
		loaders.add(FurnaceProvider.class.getClassLoader()); // this loader
																// needs to be
																// checked first
		loaders.addAll(repositoryManager.getClassLoaders());

		CompositeFurnaceClassLoader compositeLoader = new CompositeFurnaceClassLoader(
				loaders);
		Furnace furnace = FurnaceFactory.getInstance(compositeLoader,
				furnaceLoader);

		/*
		 * These native repositories need to be added before extensions due to
		 * JBDS requirements.
		 */
		Bundle runtimeBundle = Platform.getBundle(RUNTIME_PLUGIN_ID);
		File bundleFile = FileLocator.getBundleFile(runtimeBundle);
		furnace.addRepository(AddonRepositoryMode.IMMUTABLE, new File(
				bundleFile, "addon-repository"));

		for (IFurnaceRepository repo : repos) {
			furnace.addRepository(repo.getMode(), repo.getRepositoryDirectory());
		}

		/*
		 * The user-defined addon directory should be loaded last, so that
		 * same-versioned addons always come from the built in repositories when
		 * conflicts occur.
		 */
		furnace.addRepository(AddonRepositoryMode.MUTABLE, new File(
				ForgeCorePreferences.INSTANCE.getAddonDir()));

		// Using a lenient addon compatibility strategy 
		furnace.setAddonCompatibilityStrategy(AddonCompatibilityStrategies.LENIENT);
		return furnace;
	}

	public void startFurnace() {
		try {
			if (!FurnaceService.INSTANCE.isFurnaceSet()) {
				FurnaceService.INSTANCE.setFurnace(createFurnace());
			}
			FurnaceService.INSTANCE.start();
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
