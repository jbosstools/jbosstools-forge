package org.jboss.tools.forge.ui.dialog;

import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

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
import org.jboss.forge.container.AddonRegistry;
import org.jboss.forge.container.services.ExportedInstance;
import org.jboss.forge.ui.UICommand;
import org.jboss.forge.ui.UICommandMetadata;
import org.jboss.forge.ui.wizard.UIWizardStep;
import org.jboss.tools.forge.core.ForgeService;
import org.jboss.tools.forge.ui.wizards.ForgeWizard;

public class UICommandListDialog extends PopupDialog {

    private SortedMap<String, UICommand> allCandidates = null;
    private String selectedCommandName = null;
    private IStructuredSelection currentSelection = null;

    public UICommandListDialog(IWorkbenchWindow window) {
        super(window.getShell(), SWT.RESIZE, true, true, // persist size
            false, // but not location
            true, true, "Run a Forge command", "Start typing to filter the list");
        allCandidates = getAllCandidates();
        ISelection selection = window.getSelectionService().getSelection();
        if (selection instanceof IStructuredSelection)
            currentSelection = (IStructuredSelection) selection;
    }

    private SortedMap<String, UICommand> getAllCandidates() {
        SortedMap<String, UICommand> result = new TreeMap<String, UICommand>();
        AddonRegistry addonRegistry = ForgeService.INSTANCE.getAddonRegistry();
        Set<ExportedInstance<UICommand>> exportedInstances = addonRegistry.getExportedInstances(UICommand.class);
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
        for (String candidate : allCandidates.keySet()) {
            list.add(candidate);
        }
        list.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                openWizard();
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

    private void openWizard() {
        UICommand selectedCommand = allCandidates.get(selectedCommandName);
        ForgeWizard wizard = new ForgeWizard(selectedCommand, currentSelection);
        WizardDialog wizardDialog = new WizardDialog(getParentShell(), wizard);
        // TODO: Show help button when it's possible to display the docs for each UICommand
        wizardDialog.setHelpAvailable(false);
        wizardDialog.open();
    }

}
