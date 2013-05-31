package org.jboss.tools.forge.ui.wizard.rest;

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
import org.jboss.tools.forge.ui.wizard.AbstractForgeWizardPage;
import org.jboss.tools.forge.ui.wizard.util.WizardsHelper;

public class RestWizardPage extends AbstractForgeWizardPage {
	
	final static String PROJECT_NAME = "RestWizardPage.projectName";
	final static String ENTITY_NAMES = "RestWizardPage.entityNames";

	private ArrayList<String> entityNames = new ArrayList<String>();
	
	private Combo projectNameCombo;
	private Table selectEntitiesTable;
	
	protected RestWizardPage() {
		super("org.jboss.tools.forge.ui.wizard.rest", "Generate REST Endpoints", null);
	}	

	@Override
	public void createControl(Composite parent) {
		Composite control = new Composite(parent, SWT.NULL);
		control.setLayout(new GridLayout(3, false));
		createProjectEditor(control);
		createEntitiesEditor(control);
		setControl(control);
	}
	
	private void createProjectEditor(Composite parent) {
		Label projectNameLabel = new Label(parent, SWT.NONE);
		projectNameLabel.setText("Project: ");
		projectNameCombo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
		projectNameCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		IProject[] allProjects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for (IProject project : allProjects) {
			if (WizardsHelper.isJPAProject(project)) {
				projectNameCombo.add(project.getName());
			}
		}
		String projectName = (String)getWizardDescriptor().get(PROJECT_NAME); 
		if (projectName != null) {
			projectNameCombo.setText(projectName);
			handleProjectChange();
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
		getWizardDescriptor().put(PROJECT_NAME, projectNameCombo.getText());
		((RestWizard)getWizard()).handleProjectChange();
		refreshEntitiesTable();
	}
	
	public boolean isPageComplete() {
		String projectName = projectNameCombo.getText();
		boolean busy = ((RestWizard)getWizard()).isBusy();
		boolean nameComplete = projectName != null && !"".equals(projectName);
		return nameComplete && !busy;
	}
	
	void updatePageComplete() {
		setPageComplete(isPageComplete());
	}
	
	private IProject getProject(String projectName) {
		return ResourcesPlugin
				.getWorkspace()
				.getRoot()
				.getProject(projectName);
	}
}
