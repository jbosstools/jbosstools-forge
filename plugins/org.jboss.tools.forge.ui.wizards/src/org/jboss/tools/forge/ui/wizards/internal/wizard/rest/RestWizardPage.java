/**
 * Copyright (c) Red Hat, Inc., contributors and others 2013 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.forge.ui.wizards.internal.wizard.rest;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jpt.common.core.resource.java.JavaResourceAbstractType;
import org.eclipse.jpt.jpa.core.JpaProject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.jboss.tools.forge.ui.wizards.internal.wizard.AbstractForgeWizardPage;
import org.jboss.tools.forge.ui.wizards.internal.wizard.util.WizardsHelper;

public class RestWizardPage extends AbstractForgeWizardPage {
	
	final static String PROJECT_NAME = "RestWizardPage.projectName";
	final static String ENTITY_NAMES = "RestWizardPage.entityNames";
	final static String SETUP_NEEDED = "RestWizardPage.setupNeeded";
	final static String ACTIVATOR_TYPE = "RestWizardPage.activatorType";
	final static String CONTENT_TYPE = "RestWizardPage.contentType";
	
	final static String ACTIVATOR_TYPE_WEB_XML = "WEB_XML";
	final static String ACTIVATOR_TYPE_APPLICATION_CLASS = "APP_CLASS";
	final static String ACTIVATOR_TYPE_NONE = "";
	
	final static String CONTENT_TYPE_XML = "application/xml";
	final static String CONTENT_TYPE_JSON = "application/json";
	
	final static String PAGE_NAME = "org.jboss.tools.forge.ui.wizard.rest";

	private ArrayList<String> entityNames = new ArrayList<String>();
	
	private Combo projectNameCombo;
	private Combo activatorTypeCombo;
	private Combo contentTypeCombo;
	private Table selectEntitiesTable;
	
	private boolean busy = false;

	protected RestWizardPage() {
		super(PAGE_NAME, "Generate REST Endpoints", null);
	}	

	@Override
	public void createControl(Composite parent) {
		Composite control = new Composite(parent, SWT.NULL);
		control.setLayout(new GridLayout(3, false));
		createProjectEditor(control);
		createActivatorTypeEditor(control);
		createContentTypeEditor(control);
		createEntitiesEditor(control);
		setControl(control);
		initializeEditors();
	}
	
	private void initializeEditors() {
		String projectName = (String)getWizardDescriptor().get(PROJECT_NAME); 
		if (projectName != null) {
			projectNameCombo.setText(projectName);
			handleProjectChange();
		}
	}
	
	private void createContentTypeEditor(Composite parent) {
		Label contentTypeLabel = new Label(parent, SWT.NONE);
		contentTypeLabel.setText("Content type:");
		contentTypeCombo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
		contentTypeCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		contentTypeCombo.add(CONTENT_TYPE_JSON);
		contentTypeCombo.add(CONTENT_TYPE_XML);
		contentTypeCombo.setText(CONTENT_TYPE_JSON);
		getWizardDescriptor().put(CONTENT_TYPE, CONTENT_TYPE_JSON);
		contentTypeCombo.addSelectionListener(new SelectionAdapter() {			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				getWizardDescriptor().put(CONTENT_TYPE, contentTypeCombo.getText());
			}
		});
		Label filler = new Label(parent, SWT.NONE);
		filler.setText("");
	}
	
	private void createProjectEditor(Composite parent) {
		Label projectNameLabel = new Label(parent, SWT.NONE);
		projectNameLabel.setText("Project: ");
		projectNameCombo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
		projectNameCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		IProject[] allProjects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for (IProject project : allProjects) {
			if (WizardsHelper.isJPAProject(project)) {
				projectNameCombo.add(project.getName());
			}
		}
		projectNameCombo.addSelectionListener(new SelectionAdapter() {			
			@Override
			public void widgetSelected(SelectionEvent e) {
				String newProjectName = projectNameCombo.getText();
				String oldProjectName = (String)getWizardDescriptor().get(PROJECT_NAME);
				if ((oldProjectName == null && newProjectName != null) || !oldProjectName.equals(newProjectName)) {
					handleProjectChange();
				}
			}			
		});
		Label filler = new Label(parent, SWT.NONE);
		filler.setText("");
	}
	
	private void createActivatorTypeEditor(Composite parent) {
		Label activatorTypeLabel = new Label(parent, SWT.NONE);
		activatorTypeLabel.setText("Activator type:");
		activatorTypeCombo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
		activatorTypeCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		activatorTypeCombo.add(ACTIVATOR_TYPE_WEB_XML);
		activatorTypeCombo.add(ACTIVATOR_TYPE_APPLICATION_CLASS);
		activatorTypeCombo.addSelectionListener(new SelectionAdapter() {			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				getWizardDescriptor().put(ACTIVATOR_TYPE, activatorTypeCombo.getText());
			}
		});
		activatorTypeCombo.setEnabled(false);
		Label filler = new Label(parent, SWT.NONE);
		filler.setText("");
	}
	
	private void createEntitiesEditor(Composite parent) {
		Label selectEntitiesLabel = new Label(parent, SWT.NONE);
		selectEntitiesLabel.setText("Select entities:");
		GridData gridData = new GridData();
		gridData.verticalAlignment = SWT.TOP;
		gridData.horizontalSpan = 3;
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = SWT.FILL;
		selectEntitiesLabel.setLayoutData(gridData);
        selectEntitiesTable = new Table(parent, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
        gridData = new GridData();
		gridData.verticalSpan = 2;
		gridData.horizontalSpan = 2;
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        gridData.horizontalAlignment = SWT.FILL;
        gridData.verticalAlignment = SWT.FILL;
        selectEntitiesTable.setLayoutData(gridData);
        selectEntitiesTable.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (e.item instanceof TableItem) {
                    TableItem source = (TableItem) e.item;
                    if (source.getChecked()) {
                        entityNames.add(source.getText());
                    } else {
                        entityNames.remove(source.getText());
                    }
                }
            }
        });
        Button selectAllButton = new Button(parent, SWT.PUSH);
        selectAllButton.setText("Select All");
        gridData = new GridData();
        gridData.horizontalAlignment = SWT.FILL;
        gridData.verticalAlignment = SWT.TOP;
        selectAllButton.setLayoutData(gridData);
        selectAllButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
            	entityNames.clear();
            	for (TableItem tableItem : selectEntitiesTable.getItems()) {
            		tableItem.setChecked(true);
            		entityNames.add(tableItem.getText());
            	}
            }
		});
        Button selectNoneButton = new Button(parent, SWT.PUSH);
        selectNoneButton.setText("Select None");
        gridData = new GridData();
        gridData.horizontalAlignment = SWT.FILL;
        gridData.verticalAlignment = SWT.TOP;
        selectNoneButton.setLayoutData(gridData);
        selectNoneButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
            	entityNames.clear();
            	for (TableItem tableItem : selectEntitiesTable.getItems()) {
            		tableItem.setChecked(false);
            	}
            }
		});
        getWizardDescriptor().put(ENTITY_NAMES, entityNames);
		refreshEntitiesTable();
	}

	private void refreshEntitiesTable() {
		if (selectEntitiesTable == null) return;
		String projectName = (String)getWizardDescriptor().get(PROJECT_NAME);
		if (projectName == null) return;
		selectEntitiesTable.removeAll();
		IProject project = getProject(projectName);
		JpaProject jpaProject = (JpaProject)project.getAdapter(JpaProject.class);
		Iterable<JavaResourceAbstractType> iterable = jpaProject.getAnnotatedJavaSourceTypes();
		Iterator<JavaResourceAbstractType> iterator = iterable.iterator();
		while (iterator.hasNext()) {
			TableItem tableItem = new TableItem(selectEntitiesTable, SWT.NONE);
			JavaResourceAbstractType jrat = iterator.next();
			String qualifiedName = jrat.getTypeBinding().getQualifiedName();
			tableItem.setData(jrat.getFile().getLocation().toOSString());
			tableItem.setText(qualifiedName);
		}		
	}
	
	private void handleProjectChange() {
		activatorTypeCombo.setEnabled(false);
		getWizardDescriptor().put(PROJECT_NAME, projectNameCombo.getText());
		setBusy(true);
		updatePageComplete();
		checkRestSetup();
		refreshEntitiesTable();
	}
	
	private void checkRestSetup() {
		new RestSetupHelper((RestWizard)getWizard()).checkRestSetup(getWizard().getRuntime());
	}
	
	void setSetupNeeded(boolean b) {
		getWizardDescriptor().put(SETUP_NEEDED, b);
		activatorTypeCombo.setEnabled(b);
	}
	
	void setActivatorType(String activatorType) {
		if (activatorType.equals(ACTIVATOR_TYPE_NONE)) {
			getWizardDescriptor().put(ACTIVATOR_TYPE, null);
			activatorTypeCombo.deselectAll();
		} else {
			getWizardDescriptor().put(ACTIVATOR_TYPE, activatorType);
			activatorTypeCombo.setText(activatorType);
		}
	}
	
	void setBusy(boolean b) {
		busy = b;
		updatePageComplete();
	}
	
	public boolean isPageComplete() {
		String projectName = projectNameCombo.getText();
		boolean nameComplete = projectName != null && !"".equals(projectName);
		return nameComplete && !busy;
	}
	
	void updatePageComplete() {
		setPageComplete(isPageComplete());
	}
	
}
