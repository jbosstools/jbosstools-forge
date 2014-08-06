/**
 * Copyright (c) Red Hat, Inc., contributors and others 2013 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.forge.core.internal.furnace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CompositeFurnaceClassLoader extends ClassLoader
{
   private final List<ClassLoader> loaders = Collections.synchronizedList(new ArrayList<ClassLoader>());

   public CompositeFurnaceClassLoader(List<ClassLoader> loaders)
   {
      super(null);
      this.loaders.addAll(loaders);
      this.loaders.remove(this);
   }

   @Override
   public Class<?> loadClass(String name) throws ClassNotFoundException
   {
      for (ClassLoader classLoader : loaders)
      {
         try
         {
            return classLoader.loadClass(name);
         }
         catch (ClassNotFoundException notFound)
         {
            // oh well
         }
      }

      throw new ClassNotFoundException(name);
   }

   @Override
   public String toString()
   {
      return loaders.toString();
   }
}