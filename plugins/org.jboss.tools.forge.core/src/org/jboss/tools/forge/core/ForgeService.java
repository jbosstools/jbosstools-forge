/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.tools.forge.core;

import org.jboss.forge.container.AddonRegistry;
import org.jboss.forge.container.Forge;

/**
 * This is a singleton for the {@link Forge} class.
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */

public enum ForgeService
{
   INSTANCE;

   private transient Forge forge;

   private ForgeService()
   {
   }

   public void setForge(Forge forge)
   {
      this.forge = forge;
   }

   public void start(ClassLoader loader)
   {
      forge.startAsync(loader);
   }

   public AddonRegistry getAddonRegistry()
   {
      return forge.getAddonRegistry();
   }

   public void stop()
   {
      forge.stop();
   }

}
