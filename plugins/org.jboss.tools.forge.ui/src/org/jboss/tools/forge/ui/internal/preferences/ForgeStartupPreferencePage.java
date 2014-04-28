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

public class ForgeStartupPreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage {
	
	private Button startupButton;
	private Button startInDebugButton;
	private Text vmArgsText;
	private Text addonDirText;
	  
	protected Control createContents(Composite parent) {
		noDefaultAndApplyButton();
		Composite clientArea = createClientArea(parent);
		createStartupButton(clientArea);
		createStartInDebugButton(clientArea);
		createVmArgsText(clientArea);
		createAddonDirText(parent);
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
	
	private String getVmArgsText() {
		String str = vmArgsText.getText();
		return "".equals(str) ? null : str; 
	}
	
	public boolean performOk() {
		ForgeCorePreferences.INSTANCE.setStartup(startupButton.getSelection());
		ForgeCorePreferences.INSTANCE.setStartInDebug(startInDebugButton.getSelection());
		ForgeCorePreferences.INSTANCE.setVmArgs(getVmArgsText());
		ForgeCorePreferences.INSTANCE.setAddonDir(addonDirText.getText());
		return true;
	}
}
