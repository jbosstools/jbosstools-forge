package org.jboss.tools.forge.ui.internal.cli;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.ResourcesPlugin;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.shell.spi.command.CdTokenHandler;
import org.jboss.forge.addon.ui.context.UIContext;

/**
 * Handles the '#' workspace shortcut character in JBossTools Forge CLI
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class WorkspaceCdTokenHandler implements CdTokenHandler
{
   private ResourceFactory resourceFactory;

   public WorkspaceCdTokenHandler(ResourceFactory resourceFactory)
   {
      this.resourceFactory = resourceFactory;
   }

   @Override
   public List<Resource<?>> getNewCurrentResources(UIContext context, String token)
   {
      List<Resource<?>> result = new ArrayList<>();
      if (token.startsWith("#"))
      {
         File file = ResourcesPlugin.getWorkspace().getRoot().getLocation().makeAbsolute().toFile();
         Resource<File> resource = resourceFactory.create(new File(file, token.replaceFirst("#", "")));
         result.add(resource);
      }
      return result;
   }
}