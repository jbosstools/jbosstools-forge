package org.jboss.tools.forge.ui.wizards;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.jboss.forge.container.Addon;
import org.jboss.forge.container.AddonRegistry;
import org.jboss.forge.convert.Converter;
import org.jboss.forge.convert.ConverterFactory;
import org.jboss.forge.proxy.Proxies;
import org.jboss.forge.resource.Resource;
import org.jboss.forge.ui.UICommand;
import org.jboss.forge.ui.result.Result;
import org.jboss.tools.forge.core.ForgeService;
import org.jboss.tools.forge.ui.ForgeUIPlugin;
import org.jboss.tools.forge.ui.context.UIContextImpl;
import org.jboss.tools.forge.ui.context.UISelectionImpl;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class ForgeWizard extends MutableWizard
{

   private UICommand initialCommand;
   private UIContextImpl uiContext;

   public ForgeWizard(UICommand uiCommand, IStructuredSelection selection)
   {
      this.initialCommand = uiCommand;
      List<Object> selectedElements = selection == null ? Collections.EMPTY_LIST
               : selection.toList();
      this.uiContext = createContext(selectedElements);
      setNeedsProgressMonitor(true);
   }

   private UIContextImpl createContext(List<Object> selectedElements)
   {
      List<Object> result = new LinkedList<Object>();
      ConverterFactory converterFactory = ForgeService.INSTANCE
               .lookup(ConverterFactory.class);
      if (converterFactory != null)
      {
         Converter<File, Resource> converter = converterFactory
                  .getConverter(File.class, locateNativeClass(Resource.class));

         if (selectedElements.isEmpty())
         {
            // Get the Workspace directory path
            IWorkspace workspace = ResourcesPlugin.getWorkspace();
            File workspaceDirectory = workspace.getRoot().getLocation()
                     .toFile();
            Object convertedObj = converter.convert(workspaceDirectory);
            result.add(Proxies.unwrap(convertedObj));
         }
         else
         {
            for (Object object : selectedElements)
            {
               if (object instanceof IResource)
               {
                  File file = ((IResource) object).getLocation().toFile();
                  result.add(Proxies.unwrap(converter.convert(file)));
               }
               else if (object instanceof IJavaElement)
               {
                  File file;
                  try
                  {
                     file = ((IJavaElement) object)
                              .getCorrespondingResource().getLocation()
                              .toFile();
                     result.add(Proxies.unwrap(converter.convert(file)));
                  }
                  catch (JavaModelException e)
                  {
                     // TODO Auto-generated catch block
                     e.printStackTrace();
                  }
               }
               else
               {
                  System.err.println("");
               }
            }
         }
      }
      UISelectionImpl<?> selection = null;
      if (!result.isEmpty())
      {
         selection = new UISelectionImpl(result);
      }
      return new UIContextImpl(selection);
   }

   private <T> Class<T> locateNativeClass(Class<T> type)
   {
      Class<T> result = type;
      AddonRegistry registry = ForgeService.INSTANCE.getAddonRegistry();
      for (Addon addon : registry.getRegisteredAddons())
      {
         try
         {
            ClassLoader classLoader = addon.getClassLoader();
            result = (Class<T>) classLoader.loadClass(type.getName());
            break;
         }
         catch (ClassNotFoundException e)
         {
         }
      }
      return result;
   }

   @Override
   public void addPages()
   {
      addPage(new ForgeWizardPage(this, initialCommand, uiContext));
   }

   @Override
   public boolean performFinish()
   {
      try
      {
         for (IWizardPage wizardPage : getPages())
         {
            UICommand cmd = ((ForgeWizardPage) wizardPage).getUICommand();
            Result result = cmd.execute(uiContext);
            String message = result.getMessage();
            if (message == null)
            {
               message = "Command "
                        + initialCommand.getMetadata().getName()
                        + " is executed.";
            }
            writeToStatusBar(message);
         }
         return true;
      }
      catch (Exception e)
      {
         ForgeUIPlugin.log(e);
         return false;
      }
   }

   protected void writeToStatusBar(String message)
   {
      IWorkbench workbench = PlatformUI.getWorkbench();
      if (workbench == null)
         return;
      IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
      if (window == null)
         return;
      IWorkbenchPage page = window.getActivePage();
      IViewPart view = page.findView(IPageLayout.ID_PROJECT_EXPLORER);
      if (view == null)
         return;
      IViewSite site = view.getViewSite();
      IActionBars actionBars = site.getActionBars();
      if (actionBars == null)
         return;
      IStatusLineManager statusLineManager = actionBars
               .getStatusLineManager();
      if (statusLineManager == null)
         return;
      statusLineManager.setMessage(message);
   }

   protected UIContextImpl getUiContext()
   {
      return uiContext;
   }

   protected UICommand getInitialCommand()
   {
      return initialCommand;
   }
}
