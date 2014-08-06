/**
 * Copyright (c) Red Hat, Inc., contributors and others 2013 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.forge.ui.wizards.internal.wizard.persistence;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.jboss.tools.forge.ui.wizards.internal.wizard.AbstractForgeWizardPage;
import org.jboss.tools.forge.ui.wizards.internal.wizard.util.WizardsHelper;

public class PersistenceSetupWizardPage extends AbstractForgeWizardPage {
	
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
		final Combo projectNameCombo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
		IProject[] allProjects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for (IProject project : allProjects) {
			if (!WizardsHelper.isJPAProject(project)) {
				projectNameCombo.add(project.getName());
			}
		}
		String projectName = (String)getWizardDescriptor().get(PROJECT_NAME); 
		if (projectName != null) {
			projectNameCombo.setText(projectName);
		}
		projectNameCombo.addSelectionListener(new SelectionAdapter() {		
			@Override
			public void widgetSelected(SelectionEvent e) {
				getWizardDescriptor().put(PROJECT_NAME, projectNameCombo.getText());
			}
		});
		projectNameCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		final Button newProjectButton = new Button(parent, SWT.NONE);
		newProjectButton.setText("New...");
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
	
}
