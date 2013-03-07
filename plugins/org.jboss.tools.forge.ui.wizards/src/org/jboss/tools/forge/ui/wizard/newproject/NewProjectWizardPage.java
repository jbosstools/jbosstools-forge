package org.jboss.tools.forge.ui.wizard.newproject;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.jboss.tools.forge.ui.wizard.AbstractForgeWizardPage;

public class NewProjectWizardPage extends AbstractForgeWizardPage {
	
	final static String PROJECT_NAME = "NewProjectWizardPage.projectName";
	final static String PROJECT_LOCATION = "NewProjectWizardPage.projectLocation";
	
	protected NewProjectWizardPage() {
		super("org.jboss.tools.forge.ui.wizard.newproject", "Create New Project", null);
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		container.setLayout(new GridLayout(3, false));
		createNameEditor(container);
		setControl(container);
	}
	
	private void createNameEditor(Composite parent) {
		Label projectNameLabel = new Label(parent, SWT.NONE);
		projectNameLabel.setText("Name: ");
		final Text projectNameText = new Text(parent, SWT.BORDER);
		projectNameText.setText("");
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gridData.horizontalSpan = 2;
		projectNameText.setLayoutData(gridData);
		projectNameText.addModifyListener(new ModifyListener() {		
			@Override
			public void modifyText(ModifyEvent e) {
				getWizardDescriptor().put(PROJECT_NAME, projectNameText.getText());
			}
		});
	}
	
}
