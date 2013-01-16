package org.jboss.tools.forge.ui.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.jboss.tools.forge.core.preferences.ForgeRuntimesPreferences;
import org.jboss.tools.forge.ui.util.ForgeHelper;

public class StartForgePage extends WizardPage {
	
	private Button startForgeButton;

	protected StartForgePage() {
		super("org.jboss.tools.forge.ui.wizard.scaffold", "Start Forge", null);
		setMessage("Forge needs to be started for this wizard to run");
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		container.setLayout(new GridLayout(1, false));
		final Button startForgeAtEclipseStartup = new Button(container, SWT.CHECK);
		startForgeAtEclipseStartup.setText("Always start Forge when Eclipse starts");
		startForgeAtEclipseStartup.addSelectionListener(new SelectionListener() {			
			@Override
			public void widgetSelected(SelectionEvent e) {
				ForgeRuntimesPreferences.INSTANCE.setStartup(startForgeAtEclipseStartup.getSelection());
			}			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		startForgeButton = new Button(container, SWT.PUSH);
		startForgeButton.setText("Start Forge");
		startForgeButton.addSelectionListener(new SelectionListener() {			
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleStartForgeButtonSelected();
			}			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		setControl(container);
	}
	
	private void handleStartForgeButtonSelected() {
		startForgeButton.setEnabled(false);
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				ForgeHelper.startForge();
			}			
		});
	}
	
	public boolean isPageComplete() {
		return ForgeHelper.isForgeRunning();
	}
	
}
