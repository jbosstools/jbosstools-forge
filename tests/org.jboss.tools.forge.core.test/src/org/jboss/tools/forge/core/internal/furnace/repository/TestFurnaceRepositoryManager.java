/**
 * Copyright (c) Red Hat, Inc., contributors and others 2004 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.forge.core.internal.furnace.repository;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import junit.framework.TestCase;

import org.codehaus.plexus.util.FileUtils;
import org.jboss.forge.furnace.repositories.AddonRepository;
import org.jboss.forge.furnace.repositories.MutableAddonRepository;
import org.jboss.tools.forge.core.furnace.FurnaceProvider;
import org.jboss.tools.forge.core.furnace.FurnaceService;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestFurnaceRepositoryManager {
	
	public static String TEST_REPOSITORY_PROVIDER_MUTABLE_PATH;
	public static String TEST_REPOSITORY_PROVIDER_IMMUTABLE_PATH;
	
	static {
		try {
			TEST_REPOSITORY_PROVIDER_MUTABLE_PATH = Files.createTempDirectory("testRepositoryProviderMutable").toString();
			TEST_REPOSITORY_PROVIDER_IMMUTABLE_PATH = Files.createTempDirectory("testRepositoryProviderImmutable").toString();
		} catch (IOException e) {
			TestCase.fail("Failed to create temporary add on repositories: " + e.getMessage());
		}
	}
	
	@BeforeClass
	public static void setUp() throws IOException {
		FurnaceProvider.INSTANCE.startFurnace();
	}
	
	@AfterClass
	public static void tearDown() throws IOException {
		FileUtils.cleanDirectory(TEST_REPOSITORY_PROVIDER_MUTABLE_PATH);
		FileUtils.forceDelete(TEST_REPOSITORY_PROVIDER_MUTABLE_PATH);
		
		FileUtils.cleanDirectory(TEST_REPOSITORY_PROVIDER_IMMUTABLE_PATH);
		FileUtils.forceDelete(TEST_REPOSITORY_PROVIDER_IMMUTABLE_PATH);
	}

	@Test
	public void testRepositoryProviderMutable() {
		assertRepositoryExists(TEST_REPOSITORY_PROVIDER_MUTABLE_PATH, true);
	}
	
	@Test
	public void testRepositoryProviderImmutable() {
		assertRepositoryExists(TEST_REPOSITORY_PROVIDER_IMMUTABLE_PATH, false);
	}
	
	private void assertRepositoryExists(String expectedPath, boolean mutable) {
		List<AddonRepository> repositories = FurnaceService.INSTANCE.getRepositories();
		
		boolean found = false;
		for(AddonRepository repository : repositories) {
			String repoPath = repository.getRootDirectory().getPath();
			
			found = (repoPath.equals(expectedPath)) &&
					(
						(mutable && repository instanceof MutableAddonRepository) ||
						(!mutable && !(repository instanceof MutableAddonRepository))
					);
			
			if(found) {
				break;
			}
		}
		
		TestCase.assertTrue("Could not find repoistory with path '"
				+ expectedPath + "' with mutable value of '"
				+ mutable + "'", found);
	}
}
