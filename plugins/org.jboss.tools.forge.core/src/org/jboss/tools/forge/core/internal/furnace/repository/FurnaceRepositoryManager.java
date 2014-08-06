/**
 * Copyright (c) Red Hat, Inc., contributors and others 2013 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.forge.core.internal.furnace.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SafeRunner;
import org.jboss.tools.forge.core.furnace.repository.FurnaceRepository;
import org.jboss.tools.forge.core.furnace.repository.IFurnaceRepository;
import org.jboss.tools.forge.core.furnace.repository.IFurnaceRepositoryProvider;
import org.jboss.tools.forge.core.internal.ForgeCorePlugin;

/**
 * <p>
 * Manager for the <code>org.jboss.tools.forge.core.furnaceRepository</code> extension point.
 * </p>
 */
public class FurnaceRepositoryManager {
	private static final String FURNACE_REPOSITORY_EXTENSION_POINT = "furnaceRepository"; //$NON-NLS-1$
	private static final String REPOSITORY_PATH_ELEM = "repositoryPath"; //$NON-NLS-1$
	private static final String REPOSITORY_PROVIDER_ELEM = "repositoryProvider"; //$NON-NLS-1$
	private static final String PATH_ATTR = "path"; //$NON-NLS-1$
	private static final String MUTABLE_ATTR = "mutable"; //$NON-NLS-1$
	private static final String PROVIDER_ATTR = "provider"; //$NON-NLS-1$
	
	/**
	 * <p>
	 * Singleton instance.
	 * </p>
	 */
	private static FurnaceRepositoryManager handler;
	
	/**
	 * <p>
	 * {@link List} of {@link FurnaceRepository}s loaded from the extension point.
	 * </p>
	 */
	private List<IFurnaceRepository> repositories;
	
   /**
    * <p>
    * {@link List} of {@link ClassLoader}s loaded from the extension point.
    * </p>
    */
   private List<ClassLoader> loaders;
	
	/**
	 * <p>
	 * Singleton default constructor.
	 * </p>
	 */
	private FurnaceRepositoryManager() {
	}
	
	/**
	 * @return singleton {@link FurnaceRepositoryManager} instance
	 */
	public static FurnaceRepositoryManager getDefault() {
		if(handler == null) {
			handler = new FurnaceRepositoryManager();
		}
		
		return handler;
	}
	
	/**
	 * @return {@link IFurnaceRepository}s loaded from the extension point
	 */
	public List<IFurnaceRepository> getRepositories() {
		if(this.repositories == null) {
			this.load();
		}
		
		return this.repositories;
	}
	
	/**
	 * <p>
	 * Load the {@link IFurnaceRepository}s and {@link ClassLoader}s from the extension points.
	 * </p>
	 */
	private void load() {
		final List<IFurnaceRepository> repos = new ArrayList<>();
		final List<ClassLoader> loaders = new ArrayList<>();
		
		IConfigurationElement[] configElems = Platform.getExtensionRegistry().getConfigurationElementsFor(
				ForgeCorePlugin.PLUGIN_ID, FURNACE_REPOSITORY_EXTENSION_POINT);
		
		for(IConfigurationElement configElem : configElems) {
			switch (configElem.getName()) {
				// load static repository path
				case REPOSITORY_PATH_ELEM: {
					String path = configElem.getAttribute(PATH_ATTR);
					boolean mutable = Boolean.parseBoolean(configElem.getAttribute(MUTABLE_ATTR));
					
					if(path != null && ! path.isEmpty()) {
						repos.add(new FurnaceRepository(path, mutable));
					}

					break;
				}
				
				//load programmatic repository paths from a provider
				case REPOSITORY_PROVIDER_ELEM: {
					try {
						final IFurnaceRepositoryProvider provider =
								(IFurnaceRepositoryProvider)configElem.createExecutableExtension(PROVIDER_ATTR);
						
						// run extension provided code in a safe way
						SafeRunner.run(new ISafeRunnable() {
							@Override
							public void run() throws Exception {
								repos.addAll(provider.getRepositories());
								loaders.add(provider.getClassLoader());
							}
							
							@Override
							public void handleException(Throwable t) {
								ForgeCorePlugin.log(t);
							}
						});
					} catch (CoreException e) {
						ForgeCorePlugin.log(e);
					}
					break;
				}
			}
			
		}
		
		this.repositories = Collections.unmodifiableList(repos);
		this.loaders = Collections.unmodifiableList(loaders);
	}

   public List<ClassLoader> getClassLoaders()
   {
      if(loaders == null)
      {
         load();
      }
      return loaders;
   }
}
