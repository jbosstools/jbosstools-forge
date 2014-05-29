package org.jboss.tools.forge.core.internal.furnace.repository;

import java.util.ArrayList;
import java.util.List;

import org.jboss.tools.forge.core.furnace.repository.FurnaceRepository;
import org.jboss.tools.forge.core.furnace.repository.IFurnaceRepository;
import org.jboss.tools.forge.core.furnace.repository.IFurnaceRepositoryProvider;

public class TestFuranceRepositoryProvider implements
		IFurnaceRepositoryProvider {

	@Override
	public List<IFurnaceRepository> getRepositories() {
		List<IFurnaceRepository> repos = new ArrayList<>();
		
		repos.add(new FurnaceRepository(TestFurnaceRepositoryManager.TEST_REPOSITORY_PROVIDER_MUTABLE_PATH, true));
		repos.add(new FurnaceRepository(TestFurnaceRepositoryManager.TEST_REPOSITORY_PROVIDER_IMMUTABLE_PATH, false));
		
		return repos;
	}

   @Override
   public ClassLoader getClassLoader()
   {
      return TestFuranceRepositoryProvider.class.getClassLoader();
   }
}