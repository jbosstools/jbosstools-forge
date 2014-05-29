package org.jboss.tools.forge.core.furnace.repository;

import java.util.List;

/**
 * <p>
 * Interface implemented by users of the <code>org.jboss.tools.forge.core.furnaceRepository</code>
 * extension point.
 * </p>
 */
public interface IFurnaceRepositoryProvider {
	
	/**
	 * @return list of Furnace add on repositories to add to Furnace
	 */
	public List<IFurnaceRepository> getRepositories();
}
