package org.jboss.tools.forge.ui.ext.dialog;

import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.IPageChangingListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.dialogs.PageChangingEvent;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.IWorkbenchWindow;
import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.wizard.UIWizardStep;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.services.ExportedInstance;
import org.jboss.tools.forge.ext.core.FurnaceService;
import org.jboss.tools.forge.ui.ext.context.UIContextImpl;
import org.jboss.tools.forge.ui.ext.wizards.ForgeWizard;
import org.jboss.tools.forge.ui.ext.wizards.ForgeWizardPage;

public class UICommandListDialog extends PopupDialog {

	private SortedMap<String, UICommand> allCandidates = null;
	private String selectedCommandName = null;
	private IStructuredSelection currentSelection = null;

	public UICommandListDialog(IWorkbenchWindow window) {
		super(window.getShell(), SWT.RESIZE, true,
				true, // persist size
				false, // but not location
				true, true, "Run a Forge command",
				"Start typing to filter the list");
		allCandidates = getAllCandidates();
		ISelection selection = window.getSelectionService().getSelection();
		if (selection instanceof IStructuredSelection)
			currentSelection = (IStructuredSelection) selection;
	}

	private SortedMap<String, UICommand> getAllCandidates() {
		SortedMap<String, UICommand> result = new TreeMap<String, UICommand>();
		AddonRegistry addonRegistry = FurnaceService.INSTANCE
				.getAddonRegistry();
		Set<ExportedInstance<UICommand>> exportedInstances = addonRegistry
				.getExportedInstances(UICommand.class);
		for (ExportedInstance<UICommand> instance : exportedInstances) {
			UICommand uiCommand = instance.get();
			if (!(uiCommand instanceof UIWizardStep)) {
				UICommandMetadata metadata = uiCommand.getMetadata();
				result.put(metadata.getName(), uiCommand);
			}
		}
		return result;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite result = (Composite) super.createDialogArea(parent);
		result.setLayout(new FillLayout());
		final List list = new List(result, SWT.SINGLE | SWT.V_SCROLL);
		final UIContextImpl uiContext = UIContextImpl
				.createContext(currentSelection);
		for (Entry<String, UICommand> candidate : allCandidates.entrySet()) {
			if (candidate.getValue().isEnabled(uiContext)) {
				list.add(candidate.getKey());
			}
		}
		list.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				openWizard(uiContext);
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				String[] selection = list.getSelection();
				if (selection.length == 1) {
					selectedCommandName = selection[0];
				}
			}
		});
		return result;
	}

	private void openWizard(UIContextImpl context) {
		UICommand selectedCommand = allCandidates.get(selectedCommandName);
		ForgeWizard wizard = new ForgeWizard(selectedCommand, context);
		wizard.setWindowTitle(selectedCommandName);
		final WizardDialog wizardDialog = new WizardDialog(getParentShell(),
				wizard);
		// TODO: Show help button when it's possible to display the docs for
		// each UICommand
		wizardDialog.setHelpAvailable(false);
		wizardDialog.addPageChangingListener(new IPageChangingListener() {

			@Override
			public void handlePageChanging(PageChangingEvent event) {
				// Mark as unchanged
				ForgeWizardPage currentPage = (ForgeWizardPage) event
						.getCurrentPage();
				if (currentPage != null) {
					currentPage.setChanged(false);
				}
			}
		});
		wizardDialog.addPageChangedListener(new IPageChangedListener() {
			@Override
			public void pageChanged(PageChangedEvent event) {
				// BEHAVIOR: Finish button is enabled by default and then
				// disabled when any field is selected/changed in the first page

				// WHY: Wizard.canFinish() is called before getNextPage is
				// called, therefore not checking if the second page is complete

				// SOLUTION: Calling updateButtons will call canFinish() once
				// again and set the button to the correct state
				wizardDialog.updateButtons();
			}
		});
		wizardDialog.open();
	}
}
