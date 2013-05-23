package org.jboss.tools.forge.ui.wizard.scaffold;

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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.jboss.tools.forge.ui.wizard.AbstractForgeWizardPage;

public class ScaffoldEntitiesWizardPage extends AbstractForgeWizardPage {
	
	final static String ENTITY_NAMES = "ScaffoldEntitiesWizardPage.entityNames";
	
	private ArrayList<String> entityNames = new ArrayList<String>();
	private Table selectEntitiesTable;
	private Label selectEntitiesLabel;

	protected ScaffoldEntitiesWizardPage() {
		super("org.jboss.tools.forge.ui.wizard.scaffold.entities", "Select Entities", null);
	}
	
	@Override
	public void createControl(Composite parent) {
		Composite control = new Composite(parent, SWT.NULL);
		control.setLayout(new GridLayout(2, false));
		createEntitiesEditor(control);
		setControl(control);
	}
	
	private void createEntitiesEditor(Composite parent) {
		selectEntitiesLabel = new Label(parent, SWT.NONE);
		selectEntitiesLabel.setText("Select entities:");
		GridData gridData = new GridData();
		gridData.verticalAlignment = SWT.TOP;
		gridData.horizontalSpan = 2;
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = SWT.FILL;
		selectEntitiesLabel.setLayoutData(gridData);
        selectEntitiesTable = new Table(parent, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
        gridData = new GridData();
		gridData.verticalSpan = 2;
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
		String projectName = (String)getWizardDescriptor().get(ScaffoldProjectWizardPage.PROJECT_NAME);
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
			tableItem.setText(qualifiedName);
		}		
	}
	
	private void refreshEntitiesLabel() {
		if (selectEntitiesLabel == null) return;
		String labelText = "Select entities";
		String projectName = (String)getWizardDescriptor().get(ScaffoldProjectWizardPage.PROJECT_NAME);
		if (projectName != null) {
			labelText += " for project '" + projectName +"'";
		}
		labelText += " :";
		selectEntitiesLabel.setText(labelText);		
	}
	
	void handleProjectChange() {
		refreshEntitiesTable();
		refreshEntitiesLabel();
	}
		
	private IProject getProject(String projectName) {
		return ResourcesPlugin
				.getWorkspace()
				.getRoot()
				.getProject(projectName);
	}

}
