package org.jboss.tools.forge.ui.ext.wizards;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.widgets.Display;
import org.jboss.forge.container.addons.Addon;
import org.jboss.forge.container.addons.AddonRegistry;
import org.jboss.forge.convert.Converter;
import org.jboss.forge.convert.ConverterFactory;
import org.jboss.forge.proxy.Proxies;
import org.jboss.forge.resource.Resource;
import org.jboss.forge.ui.UICommand;
import org.jboss.forge.ui.result.Failed;
import org.jboss.forge.ui.result.NavigationResult;
import org.jboss.forge.ui.result.Result;
import org.jboss.forge.ui.wizard.UIWizard;
import org.jboss.tools.forge.ext.core.ForgeService;
import org.jboss.tools.forge.ui.ext.ForgeUIPlugin;
import org.jboss.tools.forge.ui.ext.context.UIContextImpl;
import org.jboss.tools.forge.ui.ext.context.UISelectionImpl;
import org.jboss.tools.forge.ui.ext.listeners.EventBus;
import org.jboss.tools.forge.ui.notifications.NotificationDialog;
import org.jboss.tools.forge.ui.notifications.NotificationType;

/**
 * A wizard implementation to handle {@link UICommand} objects
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class ForgeWizard extends MutableWizard {

	private UICommand initialCommand;
	private UIContextImpl uiContext;

	public ForgeWizard(UICommand uiCommand, IStructuredSelection selection) {
		this.initialCommand = uiCommand;
		List<Object> selectedElements = selection == null ? Collections.EMPTY_LIST
				: selection.toList();
		this.uiContext = createContext(selectedElements);
		setNeedsProgressMonitor(true);
		boolean isWizard = uiCommand instanceof UIWizard;
		setForcePreviousAndNextButtons(isWizard);
	}

	private UIContextImpl createContext(List<Object> selectedElements) {
		List<Object> result = new LinkedList<Object>();
		ConverterFactory converterFactory = ForgeService.INSTANCE
				.lookup(ConverterFactory.class);
		if (converterFactory != null) {
			Converter<File, Resource> converter = converterFactory
					.getConverter(File.class, locateNativeClass(Resource.class));

			if (selectedElements.isEmpty()) {
				// Get the Workspace directory path
				IWorkspace workspace = ResourcesPlugin.getWorkspace();
				File workspaceDirectory = workspace.getRoot().getLocation()
						.toFile();
				Object convertedObj = converter.convert(workspaceDirectory);
				result.add(Proxies.unwrap(convertedObj));
			} else {
				for (Object object : selectedElements) {
					if (object instanceof IResource) {
						File file = ((IResource) object).getLocation().toFile();
						result.add(Proxies.unwrap(converter.convert(file)));
					} else if (object instanceof IJavaElement) {
						File file;
						try {
							file = ((IJavaElement) object)
									.getCorrespondingResource().getLocation()
									.toFile();
							result.add(Proxies.unwrap(converter.convert(file)));
						} catch (JavaModelException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else {
						System.err.println("");
					}
				}
			}
		}
		UISelectionImpl<?> selection = null;
		if (!result.isEmpty()) {
			selection = new UISelectionImpl(result);
		}
		return new UIContextImpl(selection);
	}

	private <T> Class<T> locateNativeClass(Class<T> type) {
		Class<T> result = type;
		AddonRegistry registry = ForgeService.INSTANCE.getAddonRegistry();
		for (Addon addon : registry.getAddons()) {
			try {
				ClassLoader classLoader = addon.getClassLoader();
				result = (Class<T>) classLoader.loadClass(type.getName());
				break;
			} catch (ClassNotFoundException e) {
			}
		}
		return result;
	}

	@Override
	public void addPages() {
		addPage(new ForgeWizardPage(this, initialCommand, uiContext));
	}

	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		UICommand uiCommand = ((ForgeWizardPage) page).getUICommand();
		// If it's not a wizard, we don't care
		if (!(uiCommand instanceof UIWizard)) {
			return null;
		}
		UIWizard wiz = (UIWizard) uiCommand;
		NavigationResult nextCommand = null;
		try {
			nextCommand = wiz.next(getUiContext());
		} catch (Exception e) {
			// TODO: Use Eclipse logging mechanism
			e.printStackTrace();
		}
		// No next page
		if (nextCommand == null) {
			// Clear any subsequent pages that may exist (occurs when navigation
			// changes)
			List<ForgeWizardPage> pageList = getPageList();
			int idx = pageList.indexOf(page);
			pageList.subList(idx + 1, pageList.size()).clear();
			return null;
		} else {
			Class<? extends UICommand> successor = nextCommand.getNext();
			// Do we have any pages already displayed ? (Did we went back
			// already ?)
			ForgeWizardPage nextPage = (ForgeWizardPage) super
					.getNextPage(page);
			if (nextPage == null
					|| !isNextPageAssignableFrom(nextPage, successor)) {
				if (nextPage != null) {
					List<ForgeWizardPage> pageList = getPageList();
					int idx = pageList.indexOf(nextPage);
					// Clean the old pages
					pageList.subList(idx, pageList.size()).clear();
				}
				UICommand nextStep = ForgeService.INSTANCE.lookup(successor);
				nextPage = new ForgeWizardPage(this, nextStep, getUiContext());
				addPage(nextPage);
			}
			return nextPage;
		}
	}

	private boolean isNextPageAssignableFrom(ForgeWizardPage nextPage,
			Class<? extends UICommand> successor) {
		return Proxies.isInstance(successor, nextPage.getUICommand());
	}

	@Override
	public boolean performFinish() {
		try {
			for (IWizardPage wizardPage : getPages()) {
				UICommand cmd = ((ForgeWizardPage) wizardPage).getUICommand();
				Result result = cmd.execute(uiContext);
				if (result != null) {
					String message = result.getMessage();
					String title = "Forge Command";
					NotificationType type = NotificationType.INFO;
					if (message == null) {
						message = "Command "
								+ initialCommand.getMetadata().getName()
								+ " is executed.";
					}
					displayMessage(title, message, type);
					if (result instanceof Failed) {
						Throwable exception = ((Failed) result).getException();
						if (exception != null)
							ForgeUIPlugin.log(exception);
					}
				}
			}
			EventBus.INSTANCE.fireWizardFinished(uiContext);
			return true;
		} catch (Exception e) {
			ForgeUIPlugin.log(e);
			return false;
		}
	}
	
	protected void displayMessage(
			final String title, 
			final String message, 
			final NotificationType type) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				NotificationDialog.notify(title, message, type);
			}		
		});
	}

	@Override
	public boolean performCancel() {
		EventBus.INSTANCE.fireWizardClosed(uiContext);
		return true;
	}

	protected UIContextImpl getUiContext() {
		return uiContext;
	}

	protected UICommand getInitialCommand() {
		return initialCommand;
	}
}
