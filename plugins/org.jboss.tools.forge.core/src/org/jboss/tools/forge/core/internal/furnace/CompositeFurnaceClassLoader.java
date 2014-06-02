package org.jboss.tools.forge.core.internal.furnace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CompositeFurnaceClassLoader extends ClassLoader
{
   private final List<ClassLoader> loaders = Collections.synchronizedList(new ArrayList<ClassLoader>());

   public CompositeFurnaceClassLoader(List<ClassLoader> loaders)
   {
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

      ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
      if (contextLoader != null && contextLoader != this)
      {
         return contextLoader.loadClass(name);
      }
      else
      {
         throw new ClassNotFoundException(name);
      }
   }

}