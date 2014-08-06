/**
 * Copyright (c) Red Hat, Inc., contributors and others 2004 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.forge.core.furnace.repository;

import java.io.File;

import org.jboss.forge.furnace.repositories.AddonRepositoryMode;

/**
 * <p>
 * Represents a Furnace repository.
 * </p>
 */
public class FurnaceRepository implements IFurnaceRepository {
	/**
	 * <p>
	 * Directory containing the Furnace add on repository.
	 * </p>
	 */
	private final File repositoryDir;
	
	/**
	 * <p>
	 * <code>true</code> if this Furnace add on repository is mutable,
	 * <code>false</code> otherwise
	 * </p>
	 */
	private final boolean mutable;
	
	/**
	 * <p>
	 * Creates a Furnace repository.
	 * </p>
	 * 
	 * @param path
	 *            Absolute path to the Furnace add on repository
	 * @param mutable
	 *            <code>true</code> if this Furnace add on repository is
	 *            mutable, <code>false</code> otherwise
	 */
	public FurnaceRepository(String path, boolean mutable) {
		this(new File(path), mutable);
	}
	
	/**
	 * <p>
	 * Creates a Furnace repository.
	 * </p>
	 * 
	 * @param repositoryDir
	 *            Directory containing the Furnace add on repository
	 * @param mutable
	 *            <code>true</code> if this Furnace add on repository is
	 *            mutable, <code>false</code> otherwise
	 */
	public FurnaceRepository(File repositoryDir, boolean mutable) {
		super();
		this.repositoryDir = repositoryDir;
		this.mutable = mutable;
	}

	@Override
	public File getRepositoryDirectory() {
		return this.repositoryDir;
	}
	
	@Override
	public AddonRepositoryMode getMode() {
		return this.mutable ? AddonRepositoryMode.MUTABLE : AddonRepositoryMode.IMMUTABLE;
	}

	/**
	 * @return if Furnace repository is mutable
	 */
	public boolean isMutable() {
		return mutable;
	}
	
	@Override
	public String toString() {
		return "repositoryDir=" + this.repositoryDir.getPath() + ", mutable=" + this.mutable;
	}
}
