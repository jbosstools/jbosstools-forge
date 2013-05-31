package org.jboss.tools.forge.ui.ext.dialog;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.IWorkbenchWindow;
import org.jboss.forge.addon.ui.UICommand;

public class UICommandListDialog extends AbstractUICommandDialog {

	private String selectedCommandName;

	public UICommandListDialog(IWorkbenchWindow window) {
		super(window);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite result = (Composite) super.createDialogArea(parent);
		result.setLayout(new FillLayout());
		final List list = new List(result, SWT.SINGLE | SWT.V_SCROLL);
		final Map<String, UICommand> allCandidates = getAllCandidatesAsMap();
		for (String candidate : allCandidates.keySet()) {
			list.add(candidate);
		}
		list.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				openWizard(selectedCommandName,
						allCandidates.get(selectedCommandName));
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
}
