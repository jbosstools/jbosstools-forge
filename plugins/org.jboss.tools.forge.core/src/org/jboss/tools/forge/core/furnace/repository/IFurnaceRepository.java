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
 * Interface representing a Furnace add on repository.
 * </p>
 */
public interface IFurnaceRepository {
	/**
	 * @return the Furnace repository
	 */
	public File getRepositoryDirectory();
	
	/**
	 * @return the {@link AddonRepositoryMode} of the Furnace repository
	 */
	public AddonRepositoryMode getMode();
}
