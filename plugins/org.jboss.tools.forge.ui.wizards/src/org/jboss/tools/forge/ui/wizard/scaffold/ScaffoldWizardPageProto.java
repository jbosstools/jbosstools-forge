package org.jboss.tools.forge.ui.wizard.scaffold;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.jboss.tools.forge.ui.wizard.dialog.JPAProjectSelectionDialog;

public class ScaffoldWizardPageProto extends WizardPage {
	
	private IProject targetProject;

	protected ScaffoldWizardPageProto() {
		super("org.jboss.tools.forge.ui.wizard.scaffold", "Scaffold JPA Entities", null);
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		container.setLayout(new GridLayout(3, false));
		createProjectEditor(container);
		setControl(container);
	}
	
	private void createProjectEditor(Composite parent) {
		Label projectNameLabel = new Label(parent, SWT.NONE);
		projectNameLabel.setText("JPA Project: ");
		final Text projectNameText = new Text(parent, SWT.BORDER);
		projectNameText.setText("");
		projectNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		Button projectSearchButton = new Button(parent, SWT.NONE);
		projectSearchButton.setText("Search...");
		projectSearchButton.addSelectionListener(new SelectionListener() {			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				JPAProjectSelectionDialog dialog = new JPAProjectSelectionDialog(getShell());
				if (dialog.open() != SWT.CANCEL) {
					targetProject = (IProject)dialog.getResult()[0];
					projectNameText.setText(targetProject.getName());
				}
			}			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		Label generationModeLabel = new Label(parent, SWT.NONE);
		generationModeLabel.setText("Generation Mode: ");
		Button useExistingButton = new Button(parent, SWT.RADIO);
		useExistingButton.setText("Use existing entities");
		Label filler1 = new Label(parent, SWT.NONE);
		filler1.setText("");
		Label filler2 = new Label(parent, SWT.NONE);
		filler2.setText("");
		Button reverseEngineerButton = new Button(parent, SWT.RADIO);
		reverseEngineerButton.setText("Reverse engineer from database");
	}
	
	public IProject getTargetProject() {
		return targetProject;
	}
	
}
