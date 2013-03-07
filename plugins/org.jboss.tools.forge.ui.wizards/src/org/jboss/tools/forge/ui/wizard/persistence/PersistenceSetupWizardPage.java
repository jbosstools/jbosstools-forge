package org.jboss.tools.forge.ui.wizard.persistence;

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.jboss.tools.forge.ui.wizard.IForgeWizard;
import org.jboss.tools.forge.ui.wizard.WizardsPlugin;
import org.jboss.tools.forge.ui.wizard.dialog.ProjectSelectionDialog;

public class PersistenceSetupWizardPage extends WizardPage {
	
	final static String PROJECT_NAME = "PersistenceSetupWizardPage.projectName";
	final static String PROVIDER_NAME = "PersistenceSetupWizardPage.providerName";
	final static String CONTAINER_NAME = "PersistenceSetupWizardPage.containerName";
	
	private final static String defaultProvider = "HIBERNATE";
	private final static String defaultContainer = "JBOSS_AS7"; 

	
	protected PersistenceSetupWizardPage() {
		super("org.jboss.tools.forge.ui.wizard.persistence", "Set Up Persistence", null);
	}

	@Override
	public void createControl(Composite parent) {
		Composite control = new Composite(parent, SWT.NULL);
		control.setLayout(new GridLayout(3, false));
		createProjectEditor(control);
		createProviderEditor(control);
		createContainerEditor(control);
		setControl(control);
	}
	
	private void createProjectEditor(Composite parent) {
		Label projectNameLabel = new Label(parent, SWT.NONE);
		projectNameLabel.setText("Project: ");
		final Text projectNameText = new Text(parent, SWT.BORDER);
		projectNameText.setText("");
		projectNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		Button projectSearchButton = new Button(parent, SWT.NONE);
		projectSearchButton.setText("Search...");
		projectSearchButton.addSelectionListener(new SelectionListener() {			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				ProjectSelectionDialog dialog = new ProjectSelectionDialog(getShell());
				if (dialog.open() != SWT.CANCEL) {
					IProject project = (IProject)dialog.getResult()[0];
					projectNameText.setText(project.getName());
					getWizardDescriptor().put(PROJECT_NAME, projectNameText.getText());
				}
			}			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
	}
		
	private void createProviderEditor(Composite parent) {
		Label providerLabel = new Label(parent, SWT.NONE);
		providerLabel.setText("Provider: ");
		final Text providerText = new Text(parent, SWT.BORDER);
		providerText.setText("");
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gridData.horizontalSpan = 2;
		providerText.setLayoutData(gridData);
		providerText.addModifyListener(new ModifyListener() {		
			@Override
			public void modifyText(ModifyEvent e) {
				getWizardDescriptor().put(PROVIDER_NAME, providerText.getText());
			}
		});
		providerText.setText(defaultProvider);
	}
	
	private void createContainerEditor(Composite parent) {
		Label containerLabel = new Label(parent, SWT.NONE);
		containerLabel.setText("Container: ");
		final Text containerText = new Text(parent, SWT.BORDER);
		containerText.setText("");
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gridData.horizontalSpan = 2;
		containerText.setLayoutData(gridData);
		containerText.addModifyListener(new ModifyListener() {		
			@Override
			public void modifyText(ModifyEvent e) {
				getWizardDescriptor().put(CONTAINER_NAME, containerText.getText());
			}
		});
		containerText.setText(defaultContainer);
	}
	
	@Override
	public IForgeWizard getWizard() {
		IWizard result = super.getWizard();
		if (!(result instanceof IForgeWizard)) {
			RuntimeException e = new RuntimeException("Forge wizard pages need to be hosted by a Forge wizard");
			WizardsPlugin.log(e);
			throw e;
		}
		return (IForgeWizard)result;
	}
	
	private Map<Object, Object> getWizardDescriptor() {
		return getWizard().getWizardDescriptor();
	}
	
}
