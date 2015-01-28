/**
 * Copyright (c) Red Hat, Inc., contributors and others 2013 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.forge.ui.internal.preferences;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.jboss.tools.forge.core.preferences.ForgeCorePreferences;

public class ForgeMainPreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage {
	
	private Text addonDirText;
	  
	protected Control createContents(Composite parent) {
		noDefaultAndApplyButton();
		Composite clientArea = createClientArea(parent);
		createAddonDirText(clientArea);
		return clientArea;
	}
	
	
	private void createAddonDirText(Composite parent) {
		Label addonDirLabel = new Label(parent, SWT.NONE);
		addonDirLabel.setText("Forge Addon Repository Location: ");

		Composite container = new Composite(parent, SWT.NULL);
		container.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 2;
		layout.verticalSpacing = 9;
		layout.marginWidth = 0;
		layout.marginHeight = 0;

		addonDirText = new Text(container, SWT.BORDER);
		addonDirText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		addonDirText.setText(ForgeCorePreferences.INSTANCE.getAddonDir());
		Button button = new Button(container, SWT.PUSH);
		button.setText("Browse...");
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dialog = new DirectoryDialog(getShell(),
						SWT.OPEN);
				dialog.setText("Select a directory");
				dialog.setFilterPath(addonDirText.getText());
				String selectedPath = dialog.open();
				if (selectedPath != null) {
					addonDirText.setText(selectedPath);
				}
			}
		});
	}

	private Composite createClientArea(Composite parent) {
		Composite clientArea = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		clientArea.setLayout(layout);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		clientArea.setLayoutData(gridData);
		return clientArea;
	}

	public void init(IWorkbench workbench) {}
	
	public boolean performOk() {
		ForgeCorePreferences.INSTANCE.setAddonDir(addonDirText.getText());
		return true;
	}
}
