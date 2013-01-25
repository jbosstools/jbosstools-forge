package org.jboss.tools.forge.ui.dialog;

import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.IWorkbenchWindow;
import org.jboss.forge.container.AddonRegistry;
import org.jboss.forge.container.services.ExportedInstance;
import org.jboss.forge.ui.UICommand;
import org.jboss.tools.forge.core.ForgeService;

public class UICommandListDialog extends PopupDialog {

	public UICommandListDialog(IWorkbenchWindow window) {
		super(window.getShell(), 
				SWT.RESIZE, 
				true, 
				true, // persist size
				false, // but not location
				true, 
				true, 
				"Select the command you want Forge to execute",
				"Start typing to filter the list");
	}
	
	private SortedSet<String> getAllCandidates() {
		SortedSet<String> result = new TreeSet<String>();
	    AddonRegistry addonRegistry = ForgeService.INSTANCE.getAddonRegistry();
	    Set<ExportedInstance<UICommand>> exportedInstances = addonRegistry.getExportedInstances(UICommand.class);
		for (ExportedInstance<UICommand> instance : exportedInstances) {
			UICommand uiCommand = instance.get();
			result.add(uiCommand.getId().getName());
		}
		return result;
	}

	protected Control createDialogArea(Composite parent) {
		Composite result = (Composite)super.createDialogArea(parent);
		result.setLayout(new FillLayout());
		List list = new List(result, SWT.SINGLE | SWT.V_SCROLL);
		for (String candidate : getAllCandidates()) {
			list.add(candidate);
		}
		return result;
	}

}
