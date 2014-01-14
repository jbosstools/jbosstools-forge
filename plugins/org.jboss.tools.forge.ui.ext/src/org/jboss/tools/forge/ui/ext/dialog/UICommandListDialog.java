package org.jboss.tools.forge.ui.ext.dialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.part.FileEditorInput;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.metadata.UICategory;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.tools.forge.ext.core.FurnaceService;
import org.jboss.tools.forge.ui.ext.ForgeUIPlugin;
import org.jboss.tools.forge.ui.ext.quickaccess.QuickAccessContents;
import org.jboss.tools.forge.ui.ext.quickaccess.QuickAccessElement;
import org.jboss.tools.forge.ui.ext.quickaccess.impl.ForgeQuickAccessElement;
import org.jboss.tools.forge.ui.ext.quickaccess.impl.ForgeQuickAccessProvider;
import org.jboss.tools.forge.ui.notifications.NotificationType;

public class UICommandListDialog extends PopupDialog {

	private final WizardDialogHelper wizardHelper;

	public UICommandListDialog(IWorkbenchWindow window) {
		super(window.getShell(), SWT.RESIZE, true,
				true, // persist size
				false, // but not location
				true, true, "Run a Forge command",
				"Start typing to filter the list");
		ISelection selection = window.getSelectionService().getSelection();
		IStructuredSelection currentSelection = null;
		if (selection instanceof TreeSelection) {
			currentSelection = (TreeSelection) selection;
		} else {
			IFile activeEditorFile = getActiveEditorInput(window);
			if (activeEditorFile != null) {
				currentSelection = new StructuredSelection(activeEditorFile);
			}
		}
		wizardHelper = new WizardDialogHelper(getParentShell(),
				currentSelection);
	}

	/**
	 * Retrieves the {@link IFile} represented by the active editor
	 */
	private static IFile getActiveEditorInput(IWorkbenchWindow window) {
		IWorkbenchPage page = window.getActivePage();
		if (page != null) {
			IEditorPart part = page.getActiveEditor();
			if (part != null) {
				IEditorInput editorInput = part.getEditorInput();
				if (editorInput != null) {
					FileEditorInput fileEditorInput = (FileEditorInput) editorInput
							.getAdapter(FileEditorInput.class);
					if (fileEditorInput != null) {
						return fileEditorInput.getFile();
					}
				}
			}
		}
		return null;
	}

	@Override
	protected Color getBackground() {
		return getShell().getDisplay().getSystemColor(SWT.COLOR_WHITE);
	}

	@Override
	protected Color getForeground() {
		return getShell().getDisplay().getSystemColor(SWT.COLOR_BLACK);
	}

	@Override
	protected Point getDefaultSize() {
		return new Point(640, 480);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		final Composite result = (Composite) super.createDialogArea(parent);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.verticalSpacing = 9;
		result.setLayout(layout);
		final Text text = new Text(result, SWT.SEARCH | SWT.ICON_SEARCH);
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		BusyIndicator.showWhile(getShell().getDisplay(), new Runnable() {
			@Override
			public void run() {
				try {
					FurnaceService.INSTANCE.waitUntilContainerIsStarted();
				} catch (InterruptedException ie) {
					ForgeUIPlugin.log(ie);
					return;
				}
				final ForgeQuickAccessProvider[] providers;
				try {
					providers = getProviders();
				} catch (Exception e) {
					ForgeUIPlugin.log(e);
					ForgeUIPlugin.displayMessage(
							"Error has occurred. See Error Log for details",
							e.getMessage(), NotificationType.ERROR);
					return;
				}
				QuickAccessContents quickAccessContents = new QuickAccessContents(
						providers) {
					@Override
					public void updateFeedback(boolean filterTextEmpty,
							boolean showAllMatches) {
					}

					@Override
					public void doClose() {
						close();
					}

					@Override
					public QuickAccessElement getPerfectMatch(String filter) {
						for (ForgeQuickAccessProvider provider : providers) {
							QuickAccessElement elem = provider
									.getElementForId(filter);
							if (elem != null) {
								return elem;
							}
						}
						return null;
					}

					@Override
					public void handleElementSelected(String textStr,
							Object selectedElement) {
						if (selectedElement instanceof ForgeQuickAccessElement) {
							ForgeQuickAccessElement element = (ForgeQuickAccessElement) selectedElement;
							wizardHelper.openWizard(element.getLabel(),
									element.getCommand());
						}
					}
				};
				quickAccessContents.hookFilterText(text);
				quickAccessContents.createTable(result,
						Window.getDefaultOrientation());
				quickAccessContents.toggleShowAllMatches();
				text.setFocus();
			}
		});
		return result;
	}

	/**
	 * Returns each provider by a category
	 */
	private ForgeQuickAccessProvider[] getProviders() {
		Map<String, List<UICommand>> categories = new TreeMap<String, List<UICommand>>();
		for (UICommand command : wizardHelper.getAllCandidatesAsList()) {
			String categoryName = getCategoryName(wizardHelper.getContext(),
					command);
			List<UICommand> list = categories.get(categoryName);
			if (list == null) {
				list = new ArrayList<UICommand>();
				categories.put(categoryName, list);
			}
			list.add(command);
		}
		// Create Providers for each category
		Set<ForgeQuickAccessProvider> providers = new TreeSet<ForgeQuickAccessProvider>();
		for (Entry<String, List<UICommand>> entry : categories.entrySet()) {
			providers.add(new ForgeQuickAccessProvider(entry.getKey(),
					wizardHelper.getContext(), entry.getValue()));
		}
		return providers
				.toArray(new ForgeQuickAccessProvider[providers.size()]);
	}

	private String getCategoryName(UIContext context, UICommand command) {
		UICategory category = command.getMetadata(context).getCategory();
		if (category == null) {
			category = Categories.createDefault();
		}
		return category.toString();
	}
	
}
