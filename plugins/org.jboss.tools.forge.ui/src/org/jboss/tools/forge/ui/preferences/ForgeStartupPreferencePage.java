package org.jboss.tools.forge.ui.preferences;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.jboss.tools.forge.core.preferences.ForgeRuntimesPreferences;

public class ForgeStartupPreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage {
	
	private Button startupButton;
	  
	protected Control createContents(Composite parent) {
		noDefaultAndApplyButton();
		Composite clientArea = createClientArea(parent);
		createStartupButton(clientArea);
		return null;
	}
	
	private void createStartupButton(Composite parent) {
		startupButton = new Button(parent, SWT.CHECK);
		startupButton.setText("Start Forge when Eclipse starts." );
		startupButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		startupButton.setSelection(ForgeRuntimesPreferences.INSTANCE.getStartup());
	}
	
	private Composite createClientArea(Composite parent) {
		Composite clientArea = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		clientArea.setLayout(layout);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		clientArea.setLayoutData(gridData);
		return clientArea;
	}

	public void init(IWorkbench workbench) {}
	
	public boolean performOk() {
		ForgeRuntimesPreferences.INSTANCE.setStartup(startupButton.getSelection());
		return true;
	}
}
