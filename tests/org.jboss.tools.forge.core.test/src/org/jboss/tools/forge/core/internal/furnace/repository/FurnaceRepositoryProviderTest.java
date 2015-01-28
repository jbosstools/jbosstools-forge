/**
 * Copyright (c) Red Hat, Inc., contributors and others 2004 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.forge.core.internal.furnace.repository;

import java.util.ArrayList;
import java.util.List;

import org.jboss.tools.forge.core.furnace.repository.FurnaceRepository;
import org.jboss.tools.forge.core.furnace.repository.IFurnaceRepository;
import org.jboss.tools.forge.core.furnace.repository.IFurnaceRepositoryProvider;

public class FurnaceRepositoryProviderTest implements
		IFurnaceRepositoryProvider {

	@Override
	public List<IFurnaceRepository> getRepositories() {
		List<IFurnaceRepository> repos = new ArrayList<>();
		
		repos.add(new FurnaceRepository(FurnaceRepositoryManagerTest.TEST_REPOSITORY_PROVIDER_MUTABLE_PATH, true));
		repos.add(new FurnaceRepository(FurnaceRepositoryManagerTest.TEST_REPOSITORY_PROVIDER_IMMUTABLE_PATH, false));
		
		return repos;
	}

   @Override
   public ClassLoader getClassLoader()
   {
      return FurnaceRepositoryProviderTest.class.getClassLoader();
   }
}