/**
 * Copyright (c) Red Hat, Inc., contributors and others 2004 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.forge.core.furnace.repository;

import java.util.List;

/**
 * <p>
 * Interface implemented by users of the <code>org.jboss.tools.forge.core.furnaceRepository</code> extension point.
 * </p>
 */
public interface IFurnaceRepositoryProvider
{

   /**
    * @return list of Furnace add on repositories to add to Furnace
    */
   public List<IFurnaceRepository> getRepositories();

   /**
    * @return the {@link ClassLoader} instance on which Furnace should depend for {@link Class} type proxying. Typically
    *         this will be the {@link ClassLoader} that loaded your implementation of this interface.
    */
   public ClassLoader getClassLoader();
}
