package org.jboss.tools.forge.ui.dialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbenchWindow;
import org.jboss.forge.container.AddonRegistry;
import org.jboss.forge.container.services.ExportedInstance;
import org.jboss.forge.ui.UICategory;
import org.jboss.forge.ui.UICommand;
import org.jboss.forge.ui.UICommandMetadata;
import org.jboss.tools.forge.core.ForgeService;
import org.jboss.tools.forge.ui.wizards.ForgeWizard;

/**
 * Creates a tree
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
public class UICommandTreeDialog extends PopupDialog {

	public UICommandTreeDialog(IWorkbenchWindow window) {
		super(window.getShell(), SWT.RESIZE, true,
				true, // persist size
				false, // but not location
				true, true, "Select the command you want Forge to execute",
				"Start typing to filter the list");
	}

	private List<UICommand> getAllCandidates() {
		List<UICommand> result = new ArrayList<UICommand>();
		AddonRegistry addonRegistry = ForgeService.INSTANCE.getAddonRegistry();
		Set<ExportedInstance<UICommand>> exportedInstances = addonRegistry
				.getExportedInstances(UICommand.class);
		for (ExportedInstance<UICommand> instance : exportedInstances) {
			UICommand uiCommand = instance.get();
			result.add(uiCommand);
		}
		return result;
	}

	protected Control createDialogArea(Composite parent) {
		Composite result = (Composite) super.createDialogArea(parent);
		result.setLayout(new FillLayout());
		// TODO: Add standard icons to the tree and adapt to a better size
		final Tree tree = new Tree(parent, SWT.SINGLE);
		tree.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		List<UICommand> allCandidates = getAllCandidates();

		// TODO: Better sort algorithm
		Collections.sort(allCandidates, new Comparator<UICommand>() {
			@Override
			public int compare(UICommand o1, UICommand o2) {
				return o1.getMetadata().toString()
						.compareTo(o2.getMetadata().toString());
			}
		});

		for (UICommand cmd : allCandidates) {
			UICommandMetadata metadata = cmd.getMetadata();
			UICategory rootCategory = metadata.getCategory();

			TreeItem parentNode = null;
			if (rootCategory != null) {
				parentNode = findItem(tree, rootCategory);
			}

			TreeItem treeItem;
			if (parentNode == null) {
				treeItem = new TreeItem(tree, SWT.NONE);
				// Create categories
				UICategory cat = rootCategory;
				while (cat != null) {
					treeItem.setText(cat.getName());
					treeItem = new TreeItem(treeItem, SWT.NONE);
					cat = cat.getSubCategory();
				}
			} else {
				treeItem = new TreeItem(parentNode, SWT.NONE);
			}
			treeItem.setText(metadata.getName());
			treeItem.setData(cmd);
		}

		tree.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				TreeItem item = (TreeItem) e.item;
				UICommand selectedItem = (UICommand) item.getData();
				if (selectedItem != null) {
					openWizard(selectedItem);
				}
			}
		});
		return result;
	}

	private TreeItem findItem(Tree tree, UICategory category) {
		for (TreeItem child : tree.getItems()) {
			TreeItem itemFound = findItem(child, category);
			if (itemFound != null) {
				return itemFound;
			}
		}
		return null;
	}

	private TreeItem findItem(TreeItem root, UICategory category) {
		if (category == null) {
			return root;
		}
		for (TreeItem child : root.getItems()) {
			if (child.getText().equals(category.getName())) {
				return findItem(child, category.getSubCategory());
			}
		}
		return null;
	}

	private void openWizard(UICommand selectedCommand) {
		ForgeWizard wizard = new ForgeWizard(selectedCommand);
		WizardDialog wizardDialog = new WizardDialog(getParentShell(), wizard);
		wizardDialog.open();
	}

}
