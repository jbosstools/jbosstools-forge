package org.jboss.tools.forge.ui.internal.preferences;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.jboss.tools.forge.core.preferences.ForgeCorePreferences;

public class ForgeStartupPreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage {
	
	private Button startupButton;
	private Button startInDebugButton;
	private Text vmArgsText;
	  
	protected Control createContents(Composite parent) {
		noDefaultAndApplyButton();
		Composite clientArea = createClientArea(parent);
		createStartupButton(clientArea);
		createStartInDebugButton(clientArea);
		createVmArgsText(clientArea);
		return null;
	}
	
	private void createStartupButton(Composite parent) {
		startupButton = new Button(parent, SWT.CHECK);
		startupButton.setText("Start Forge when workbench starts." );
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		startupButton.setLayoutData(gridData);
		startupButton.setSelection(ForgeCorePreferences.INSTANCE.getStartup());
	}
	
	private void createStartInDebugButton(Composite parent) {
		startInDebugButton = new Button(parent, SWT.CHECK);
		startInDebugButton.setText("Start Forge in Debug Mode." );
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		startInDebugButton.setLayoutData(gridData);
		startInDebugButton.setSelection(ForgeCorePreferences.INSTANCE.getStartInDebug());
	}
	
	private void createVmArgsText(Composite parent) {
		Label vmArgsLabel = new Label(parent, SWT.NONE);
		vmArgsLabel.setText("Forge Startup VM Arguments: ");
		vmArgsText = new Text(parent, SWT.BORDER);
		vmArgsText.setLayoutData(new GridData(GridData.FILL_BOTH));
		String vmArgs = ForgeCorePreferences.INSTANCE.getVmArgs();
		vmArgsText.setText(vmArgs == null ? "" : vmArgs);
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
	
	private String getVmArgsText() {
		String str = vmArgsText.getText();
		return "".equals(str) ? null : str; 
	}
	
	public boolean performOk() {
		ForgeCorePreferences.INSTANCE.setStartup(startupButton.getSelection());
		ForgeCorePreferences.INSTANCE.setStartInDebug(startInDebugButton.getSelection());
		ForgeCorePreferences.INSTANCE.setVmArgs(getVmArgsText());
		return true;
	}
}
